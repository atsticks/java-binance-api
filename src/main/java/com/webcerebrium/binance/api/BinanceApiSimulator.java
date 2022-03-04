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

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.webcerebrium.binance.datatype.*;
import com.webcerebrium.binance.websocket.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class BinanceApiSimulator extends BinanceApiDefault{

    private TestAccountManager testAccountManager = new TestAccountManager();
    private TestOrderManager testOrderManager = new TestOrderManager();

    public BinanceApiSimulator(String apiKey, String secretKey) throws BinanceApiException {
        super(apiKey, secretKey);
    }

    public void initialize(){
        super.initialize();
        BinanceAccount account = getAccount();
        testAccountManager.initAccount(account);
    }

    @Override
    public List<BinanceHistoricalTrade> getHistoricalTrades(BinanceHistoricalTradesRequest request) throws BinanceApiException {
        return super.getHistoricalTrades(request);
    }

    @Override
    public List<BinanceAggregatedTrades> getAggregatedTrades(BinanceAggregatedTradesRequest request) throws BinanceApiException {
        return super.getAggregatedTrades(request);
    }

    @Override
    public BinanceAccount getAccount() throws BinanceApiException {
        return testAccountManager.getAccount();
    }

    @Override
    public List<BinanceOrder> getOpenOrders() throws BinanceApiException {
        return testOrderManager.getOpenOrders();
    }

    @Override
    public List<BinanceOrder> getOpenOrders(BinanceOpenOrderRequest request) throws BinanceApiException {
        return testOrderManager.getOpenOrders(request);
    }

    @Override
    public List<BinanceOrder> cancelOpenOrder(BinanceDeleteOrderRequest request) throws BinanceApiException {
        return testOrderManager.cancelOpenOrder(request);
    }

    @Override
    public List<BinanceOrder> getOrders(BinanceAllOrderRequest request) throws BinanceApiException {
        return testOrderManager.getOrders(request);
    }

    @Override
    public List<BinanceOrder> geClosedOrders(BinanceClosedOrderRequest request) throws BinanceApiException {
        return testOrderManager.geClosedOrders(request);
    }

    @Override
    public List<BinanceOrder> getOrders(String symbol, Long orderId, int limit) throws BinanceApiException {
        return testOrderManager.getOrders(symbol, orderId, limit);
    }

    @Override
    public List<BinanceTrade> getMyTrades(BinanceMyTradesRequest request) throws BinanceApiException {
        return testOrderManager.getMyTrades(request);
    }

    @Override
    public List<BinanceTrade> getTrades(String symbol, int limit) throws BinanceApiException {
        return testOrderManager.getTrades(symbol, limit);
    }

    @Override
    public BinanceOrder getOrder(BinanceOrderRef orderRef) {
        return testOrderManager.getOrder(orderRef);
    }

    @Override
    public BinanceOrder getOrder(BinanceOrderRequest request) throws BinanceApiException {
        return testOrderManager.getOrder(request);
    }

    @Override
    public BinanceOrderRef createOrder(BinanceOrderPlacement orderPlacement) throws BinanceApiException {
        return testOrderManager.createOrder(orderPlacement);
    }

    @Override
    public BinanceOrderRef createTestOrder(BinanceOrderPlacement orderPlacement) throws BinanceApiException {
        return testOrderManager.createTestOrder(orderPlacement);
    }

    @Override
    public BinanceDeletedOrder deleteOrderById(String symbol, Long orderId) throws BinanceApiException {
        return testOrderManager.deleteOrderById(symbol, orderId);
    }

    @Override
    public BinanceDeletedOrder deleteOrderByOrigClientId(String symbol, String origClientOrderId) throws BinanceApiException {
        return testOrderManager.deleteOrderByOrigClientId(symbol, origClientOrderId);
    }

    @Override
    public BinanceDeletedOrder deleteOrderByNewClientId(String symbol, String newClientOrderId) throws BinanceApiException {
        return testOrderManager.deleteOrderByNewClientId(symbol, newClientOrderId);
    }

    @Override
    public void deleteUserDataStream(String listenKey) throws BinanceApiException {
        super.deleteUserDataStream(listenKey);
    }

    @Override
    public List<BinanceFiatOrder> getFiatOrders(BinanceFiatOrderRequest request) throws BinanceApiException {
        return super.getFiatOrders(request);
    }

    @Override
    public List<BinanceFiatPayment> getFiatPayments(BinanceFiatOrderRequest request) throws BinanceApiException {
        return super.getFiatPayments(request);
    }

    @Override
    public Session getWebsocketSession(String url, WebSocketAdapter adapter) throws BinanceApiException {
        return super.getWebsocketSession(url, adapter);
    }

    @Override
    public String withdraw(BinanceWithdrawOrder withdrawOrder) throws BinanceApiException {
        return super.withdraw(withdrawOrder);
    }

    @Override
    public List<BinanceWithdrawTransaction> getWithdrawHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        return super.getWithdrawHistory(historyFilter);
    }

    @Override
    public List<BinanceDepositTransaction> getDepositHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        return super.getDepositHistory(historyFilter);
    }
}


