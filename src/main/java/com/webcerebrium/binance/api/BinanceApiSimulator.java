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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.datatype.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class BinanceApiSimulator implements BinanceApi{

    private BinanceApiDefault defaultApi;
    private TestAccountManager testAccountManager;
    private TestOrderManager testOrderManager;
    private TestDepositManager depositManager;
    private Map<String,MockSession> sessions = new ConcurrentHashMap<>();
    private Set<String> sessionKeys = new HashSet<>();

    public BinanceApiSimulator(BinanceApiDefault defaultApi) throws BinanceApiException {
        this.defaultApi = Objects.requireNonNull(defaultApi);
        testAccountManager = new TestAccountManager();
        testOrderManager = new TestOrderManager(testAccountManager);
        depositManager = new TestDepositManager(testAccountManager);
    }

    public void initialize(){
        BinanceAccount account = getAccount();
        testAccountManager.initAccount(defaultApi);
        testOrderManager.init();
        depositManager.initDeposits();
    }

    @Override
    public boolean ping() {
        return defaultApi.ping();
    }

    @Override
    public Long getServerTime() throws BinanceApiException {
        return defaultApi.getServerTime();
    }

    @Override
    public BinanceNodeInfo getNodeInfo() throws BinanceApiException {
        return defaultApi.getNodeInfo();
    }

    @Override
    public List<BinancePeer> getPeers() throws BinanceApiException {
        return defaultApi.getPeers();
    }

    @Override
    public BinanceDepth getDepth(String symbol) throws BinanceApiException {
        return defaultApi.getDepth(symbol);
    }

    @Override
    public BinanceDepth getDepth(String symbol, int limit) throws BinanceApiException {
        return defaultApi.getDepth(symbol, limit);
    }

    @Override
    public JsonObject getOptionInfo() throws BinanceApiException {
        return defaultApi.getOptionInfo();
    }

    @Override
    public JsonObject getSpotTickers() throws BinanceApiException {
        return defaultApi.getSpotTickers();
    }

    @Override
    public JsonObject getMarkPrice(String symbol) throws BinanceApiException {
        return defaultApi.getMarkPrice(symbol);
    }

    @Override
    public List<BinancePair> getIsolatedPairs(Integer recvWindow) throws BinanceApiException {
        return defaultApi.getIsolatedPairs(recvWindow);
    }

    @Override
    public List<BinancePair> getCrossMargingPairs() throws BinanceApiException {
        return defaultApi.getCrossMargingPairs();
    }

    @Override
    public List<BinanceHistoricalTrade> getHistoricalTrades(BinanceHistoricalTradesRequest request) throws BinanceApiException {
        return testOrderManager.getHistoricalTrades(request);
    }

    @Override
    public List<BinanceAggregatedTrades> getAggregatedTrades(BinanceAggregatedTradesRequest request) throws BinanceApiException {
        return testOrderManager.getAggregatedTrades(request);
    }

    @Override
    public List<BinanceCandlestick> getCandlestickBars(CandlestickBarRequest request) throws BinanceApiException {
        return defaultApi.getCandlestickBars(request);
    }

    @Override
    public BinanceExchangeInfo getExchangeInfo() throws BinanceApiException {
        return defaultApi.getExchangeInfo();
    }

    @Override
    public List<BinanceTicker24> get24HrPriceStatistics() throws BinanceApiException {
        return defaultApi.get24HrPriceStatistics();
    }

    @Override
    public BinanceTicker24 get24HrPriceStatistics(String symbol) throws BinanceApiException {
        return defaultApi.get24HrPriceStatistics(symbol);
    }

    @Override
    public Double getPrice(String symbol) throws BinanceApiException {
        return defaultApi.getPrice(symbol);
    }

    @Override
    public Map<String, Double> getPrices() throws BinanceApiException {
        return defaultApi.getPrices();
    }

    @Override
    public BinanceAveragePrice getAveragePrice(String symbol) throws BinanceApiException {
        return defaultApi.getAveragePrice(symbol);
    }

    @Override
    public List<BinanceTicker> getBookTickers() throws BinanceApiException {
        return defaultApi.getBookTickers();
    }

    @Override
    public BinanceTicker getBookTicker(String symbol) throws BinanceApiException {
        return defaultApi.getBookTicker(symbol);
    }

    @Override
    public BinanceAccount getAccount() throws BinanceApiException {
        return testAccountManager.getAccount();
    }

    @Override
    public BinanceTradeFee getTradeFee(String symbol, Integer recvWindow) throws BinanceApiException {
        return defaultApi.getTradeFee(symbol, recvWindow);
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
    public List<BinanceTrade> getMyTrades(BinanceTradesRequest request) throws BinanceApiException {
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
    public BinanceOrder deleteOrderById(String symbol, Long orderId) throws BinanceApiException {
        return testOrderManager.deleteOrderById(symbol, orderId);
    }

    @Override
    public BinanceOrder deleteOrderByClientOrderId(String symbol, String clientOrderId) throws BinanceApiException {
        return testOrderManager.deleteOrderByClientOrderId(symbol, clientOrderId);
    }

    @Override
    public List<BinanceFiatOrder> getFiatOrders(BinanceFiatOrderRequest request) throws BinanceApiException {
        return depositManager.getFiatOrders(request);
    }

    @Override
    public List<BinanceFiatPayment> getFiatPayments(BinanceFiatOrderRequest request) throws BinanceApiException {
        return depositManager.getFiatPayments(request);
    }

    @Override
    public String withdraw(BinanceWithdrawOrder withdrawOrder) throws BinanceApiException {
        return depositManager.withdraw(withdrawOrder);
    }

    @Override
    public List<BinanceWithdrawTransaction> getWithdrawHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        return depositManager.getWithdrawHistory(historyFilter);
    }

    @Override
    public List<BinanceDepositTransaction> getDepositHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        return depositManager.getDepositHistory(historyFilter);
    }

    @Override
    public BinanceSystemStatus getSystemStatus() throws BinanceApiException {
        return defaultApi.getSystemStatus();
    }

    @Override
    public void deleteUserDataStream(String listenKey) throws BinanceApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public String startUserDataStream() throws BinanceApiException {
        String id = UUID.randomUUID()+"-userdata";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepUserDataStream(String listenKey) throws BinanceApiException {
        if(!sessionKeys.contains(listenKey)){
            throw new BinanceApiException("No such listen key: " + listenKey);
        }
    }

    @Override
    public String startIsolatedMarginStream() throws BinanceApiException {
        String id = UUID.randomUUID()+"-isolated";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepIsolatedMarginStream(String listenKey) throws BinanceApiException {
        // ignore
    }

    @Override
    public void deleteIsolatedMarginStream(String listenKey) throws BinanceApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public String startMarginStream() throws BinanceApiException {
        String id = UUID.randomUUID()+"-margin";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepMarginStream(String listenKey) throws BinanceApiException {
        // ignore
    }

    @Override
    public void deleteMarginStream(String listenKey) throws BinanceApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public Session getWebsocketSession(String url, WebSocketAdapter adapter) throws BinanceApiException {
        MockSession session = new MockSession(url, adapter);
        this.sessions.put(url, session);
        adapter.onWebSocketConnect(session);
        return session;
    }


    private class MockSession implements Session {

        private final String url;
        private final WebSocketAdapter adapter;
        private Gson gson = new Gson();
        
        private WebSocketPolicy clientPolicy = WebSocketPolicy.newClientPolicy();
        private InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved("simluator.binance.com", 8899);
        private InetSocketAddress localAddress = InetSocketAddress.createUnresolved("127.0.0.1", 888);
        private boolean closed;

        public MockSession(String url, WebSocketAdapter adapter) {
            this.url = url;
            this.adapter = adapter;
        }

        @Override
        public void close() {
            close(new CloseStatus(401, "Timeout"));
        }

        @Override
        public void close(CloseStatus closeStatus) {
            close(401, "Timeout");
        }

        @Override
        public void close(int i, String s) {
            this.adapter.onWebSocketClose(i, s);
            closed = true;
        }

        @Override
        public void disconnect() throws IOException {
            close(300, "Disconnected");
        }

        @Override
        public long getIdleTimeout() {
            return 1000;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return localAddress;
        }

        @Override
        public WebSocketPolicy getPolicy() {
            return clientPolicy;
        }

        @Override
        public String getProtocolVersion() {
            return "1.0";
        }

        @Override
        public RemoteEndpoint getRemote() {
            return Mockito.mock(RemoteEndpoint.class);
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return remoteAddress;
        }

        @Override
        public UpgradeRequest getUpgradeRequest() {
            return Mockito.mock(UpgradeRequest.class);
        }

        @Override
        public UpgradeResponse getUpgradeResponse() {
            return Mockito.mock(UpgradeResponse.class);
        }

        @Override
        public boolean isOpen() {
            return !closed;
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public void setIdleTimeout(long l) {
            // ignore
        }

        @Override
        public SuspendToken suspend() {
            return Mockito.mock(SuspendToken.class);
        }

        public void pushAccountUpdate(BinanceAccount account){
            JsonObject o = new JsonObject();
            o.addProperty("e", "outboundAccountInfo");
            o.get("E").getAsLong();
            o.addProperty("m", 0L);
            o.addProperty("t", 0L);
            o.addProperty("b", 0L);
            o.addProperty("s", 0L);
            o.addProperty("T", true);
            o.addProperty("W", true);
            o.addProperty("D", true);
            JsonArray b = new JsonArray();
            o.add("B", b);
            for(BinanceAsset asset:account.getAssets().values()){
                JsonElement at = gson.toJsonTree(asset);
                b.add(at);
            }
            adapter.onWebSocketText(gson.toJson(o));
        }

        public void pushAssetUpdate(BinanceAsset asset){
            JsonObject o = new JsonObject();
            o.addProperty("e", "outboundAccountInfo");
            o.get("E").getAsLong();
            o.addProperty("m", 0L);
            o.addProperty("t", 0L);
            o.addProperty("b", 0L);
            o.addProperty("s", 0L);
            o.addProperty("T", true);
            o.addProperty("W", true);
            o.addProperty("D", true);
            JsonArray b = new JsonArray();
            o.add("B", b);
            JsonElement at = gson.toJsonTree(asset);
            b.add(at);
            adapter.onWebSocketText(gson.toJson(o));
        }

        public void pushOrderUpdate(BinanceOrder order, BinanceTrade trade){
            JsonObject o = new JsonObject();
            o.addProperty("e", "executionReport");
            o.addProperty("E", order.getTime());
            o.addProperty("s", order.getSymbol());
            o.addProperty("c", order.getClientOrderId());

            o.addProperty("S", order.getSide().toString()); // was using "c" again
            o.addProperty("o", order.getType().toString());
            o.addProperty("f", order.getTimeInForce().toString());

            o.addProperty("p", order.getPrice());
            o.addProperty("q", order.getExecutedQty());

            o.addProperty("x", order.getStatus().toString()); // BinanceExecutionType
            o.addProperty("X", order.getStatus().toString()); // BinanceExecutionType
            o.addProperty("r", BinanceRejectReason.NONE.toString());

            o.addProperty("i", order.getOrderId());
            o.addProperty("l", order.getExecutedQty());
            o.addProperty("z", order.getExecutedQty());
            o.addProperty("L", order.getPrice());

            o.addProperty("L", trade.getCommission());

            o.addProperty("T", trade.getTime());
            o.addProperty("t", trade.getId());
            o.addProperty("m", trade.isMaker());
            adapter.onWebSocketText(gson.toJson(o));
        }

    }
}


