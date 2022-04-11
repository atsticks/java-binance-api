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

    private BinanceApi api;
    private BinanceAccount account;
    private BinanceExchangeInfo exchangeInfo;

    public TestAccountManager(BinanceApi api){
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

    public BinanceAccount getAccount()throws BinanceApiException {
        checkService();
        return account;
    }

    public BinanceTrade adaptBalance(BinanceOrder order) throws BinanceApiException{
        checkService();
        String symbol = order.getSymbol();
        BinanceExchangeSymbol exchangeData = exchangeInfo.getSymbol(symbol);
        String baseCoin = exchangeData.getBaseAsset();
        String targetCoin = exchangeData.getQuoteAsset();
        BinanceAsset baseAsset = account.getAsset(baseCoin);
        BinanceAsset targetAsset = account.getAsset(targetCoin);
        if(baseAsset == null){
            throw new BinanceApiException("Unknown base coin: " + baseCoin + " in symbol " + symbol);
        }
        if(targetAsset == null){
            throw new BinanceApiException("Unknown target coin: " + targetCoin + " in symbol " + symbol);
        }
        BinanceTrade trade = new BinanceTrade();
        trade.setTime(System.currentTimeMillis());
        trade.setBestMatch(true);
        switch(order.getSide()){
            case BUY:
                double amount = order.getOrigQty();
                double commission = amount *0.001;
                trade.setBuyer(true);
                trade.setCommission(amount*0.001);
                trade.setCommissionAsset(baseCoin);
                if(baseAsset.getFree()<(amount+commission)){
                    throw new BinanceApiException("Not enough balance on " + baseCoin + ", required: " + amount + ", available: " + baseAsset.getFree());
                }
                baseAsset.setFree(baseAsset.getFree()-amount);
                baseAsset.setFree(baseAsset.getFree()-commission);
                targetAsset.setFree(targetAsset.getFree()-amount * order.getPrice());
                log.info("New free balance " + baseCoin + " " + baseAsset.getFree());
                log.info("New free balance " + targetCoin + " " + targetAsset.getFree());
                order.setExecutedQty(amount);
                order.setStatus(BinanceOrderStatus.FILLED);
                break;
            case SELL:
                amount = order.getOrigQty();
                double targetAmount = amount * order.getPrice();
                commission = targetAmount*0.001;
                trade.setCommission(commission);
                trade.setCommissionAsset(targetCoin);
                trade.setBuyer(false);
                trade.setCommissionAsset(targetCoin);
                if(targetAsset.getFree()<(targetAmount+commission)){
                    throw new BinanceApiException("Not enough balance on " + baseCoin + ", required: " + amount + ", available: " + baseAsset.getFree());
                }
                targetAsset.setFree(targetAsset.getFree()-targetAmount);
                targetAsset.setFree(targetAsset.getFree()-commission);
                baseAsset.setFree(baseAsset.getFree()+amount);
                log.info("New free balance " + baseCoin + " " + baseAsset.getFree());
                log.info("New free balance " + targetCoin + " " + targetAsset.getFree());
                order.setExecutedQty(amount);
                order.setStatus(BinanceOrderStatus.FILLED);
                break;
        }
        trade.setQty(order.getExecutedQty());
        return trade;
    }

    public void adaptBalance(BinanceWithdrawOrder order) {
        checkService();
        BinanceAsset asset = account.getAsset(order.getCoin());
        if(asset!=null){
            if(asset.getFree()<order.getAmount()){
                throw new BinanceApiException("Insufficient fiat balance for " + asset.getName() + ", required: " + order.getAmount() + ", free: " + asset.getFree());
            }
            asset.setFree(asset.getFree()-order.getAmount());
        }
    }

    public void adaptBalance(BinanceFiatPayment order) {
        checkService();
        BinanceAsset asset = account.getAsset(order.getCryptoCurrency());
        if(asset!=null){
            asset.setFree(asset.getFree()+order.getObtainAmount());
        }
    }
}
