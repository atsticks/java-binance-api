/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class TestAccountManager {

    private Api api;
    private Account account;
    private ExchangeInfo exchangeInfo;

    public TestAccountManager(Api api){
        this.api = Objects.requireNonNull(api);
        checkService();
    }

    private void checkService() {
        try {
            if(account==null)
                account = api.getAccount();
            if(exchangeInfo==null)
                exchangeInfo = api.getExchangeInfo();
        }catch(Exception e){
            log.error("Error initializing account manager.", e);
        }
    }

    public Account getAccount()throws ApiException {
        checkService();
        return account;
    }

    public Trade adaptBalance(Order order) throws ApiException {
        checkService();
        String symbol = order.getSymbol();
        ExchangeSymbol exchangeData = exchangeInfo.getSymbol(symbol);
        String baseCoin = exchangeData.getBaseAsset();
        String targetCoin = exchangeData.getQuoteAsset();
        Asset baseAsset = account.getAsset(baseCoin);
        Asset targetAsset = account.getAsset(targetCoin);
        if(baseAsset == null){
            throw new ApiException("Unknown base coin: " + baseCoin + " in symbol " + symbol);
        }
        if(targetAsset == null){
            throw new ApiException("Unknown target coin: " + targetCoin + " in symbol " + symbol);
        }
        double amount = order.getOrigQty();
        double price = 0.0;
        if(order.getPrice()!=null){
            price = order.getPrice();
        }else{
            Double d = api.getPrice(order.getSymbol());
            if(d!=null){
                price = d;
            }
        }
        order.setPrice(price);

        Trade trade = new Trade();
        trade.setTime(System.currentTimeMillis());
        trade.setBestMatch(true);

        if(price==0.0){
            log.warn("No price available for order of {}", symbol);
            order.setExecutedQty(0.0);
            trade.setBestMatch(false);
            order.setStatus(OrderStatus.REJECTED);
            trade.setCommission(0.0);
        }else {
            double quoteAmount = amount * price;
            double commission = quoteAmount * 0.001;

            order.setExecutedQty(amount);
            order.setStatus(OrderStatus.FILLED);
            trade.setCommission(commission);
            trade.setCommissionAsset(targetCoin);

            switch(order.getSide()){
                case BUY:
                    trade.setBuyer(true);
                    if(targetAsset.getFree()<(quoteAmount+commission)){
                        log.warn("Not enough balance on {} , required: {}, available: {}, correcting...", targetCoin, quoteAmount, targetAsset.getFree());
                        quoteAmount = targetAsset.getFree() - commission;
                        amount = targetAsset.getFree() / price;
                    }
                    targetAsset.setFree(targetAsset.getFree()-quoteAmount);
                    targetAsset.setFree(targetAsset.getFree()-commission);
                    baseAsset.setFree(baseAsset.getFree()+amount);
                    break;
                case SELL:
                    trade.setBuyer(false);
                    if(baseAsset.getFree()<amount){
                        log.warn("Not enough balance on {} , required: {}, available: {}, correcting...", baseCoin, amount, baseAsset.getFree());
                        amount = baseAsset.getFree();
                        quoteAmount = baseAsset.getFree() * price;
                    }
                    targetAsset.setFree(targetAsset.getFree()+quoteAmount);
                    targetAsset.setFree(targetAsset.getFree()-commission);
                    baseAsset.setFree(baseAsset.getFree()-amount);
                    break;
            }
        }
        trade.setQty(amount);
        order.setExecutedQty(amount);
        log.info("New free balance " + baseCoin + " " + baseAsset.getFree());
        log.info("New free balance " + targetCoin + " " + targetAsset.getFree());
        return trade;
    }

    public void adaptBalance(WithdrawOrder order) {
        checkService();
        Asset asset = account.getAsset(order.getCoin());
        if(asset!=null){
            if(asset.getFree()<order.getAmount()){
                throw new ApiException("Insufficient fiat balance for " + asset.getName() + ", required: " + order.getAmount() + ", free: " + asset.getFree());
            }
            asset.setFree(asset.getFree()-order.getAmount());
        }
    }

    public void adaptBalance(FiatPayment order) {
        checkService();
        Asset asset = account.getAsset(order.getCryptoCurrency());
        if(asset!=null){
            asset.setFree(asset.getFree()+order.getObtainAmount());
        }
    }
}
