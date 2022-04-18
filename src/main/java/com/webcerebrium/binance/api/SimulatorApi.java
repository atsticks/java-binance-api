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
public class SimulatorApi implements Api {

    private DefaultApi defaultApi;
    private TestAccountManager testAccountManager;
    private TestOrderManager testOrderManager;
    private TestDepositManager depositManager;
    private Map<String,MockSession> sessions = new ConcurrentHashMap<>();
    private Set<String> sessionKeys = new HashSet<>();

    public SimulatorApi(DefaultApi defaultApi) throws ApiException {
        this.defaultApi = Objects.requireNonNull(defaultApi);
        this.testAccountManager = new TestAccountManager(defaultApi);
        this.testOrderManager = new TestOrderManager(testAccountManager);
        this.depositManager = new TestDepositManager(testAccountManager);
    }

    public void initialize(){

    }

    @Override
    public boolean ping() {
        return defaultApi.ping();
    }

    @Override
    public Long getServerTime() throws ApiException {
        return defaultApi.getServerTime();
    }

    @Override
    public NodeInfos getNodeInfo() throws ApiException {
        return defaultApi.getNodeInfo();
    }

    @Override
    public List<Peer> getPeers() throws ApiException {
        return defaultApi.getPeers();
    }

    @Override
    public Depth getDepth(String symbol) throws ApiException {
        return defaultApi.getDepth(symbol);
    }

    @Override
    public Depth getDepth(String symbol, int limit) throws ApiException {
        return defaultApi.getDepth(symbol, limit);
    }

    @Override
    public JsonObject getOptionInfo() throws ApiException {
        return defaultApi.getOptionInfo();
    }

    @Override
    public JsonObject getSpotTickers() throws ApiException {
        return defaultApi.getSpotTickers();
    }

    @Override
    public JsonObject getMarkPrice(String symbol) throws ApiException {
        return defaultApi.getMarkPrice(symbol);
    }

    @Override
    public List<MarketPair> getIsolatedPairs(Integer recvWindow) throws ApiException {
        return defaultApi.getIsolatedPairs(recvWindow);
    }

    @Override
    public List<MarketPair> getCrossMargingPairs() throws ApiException {
        return defaultApi.getCrossMargingPairs();
    }

    @Override
    public List<HistoricalTrade> getHistoricalTrades(HistoricalTradesRequest request) throws ApiException {
        return testOrderManager.getHistoricalTrades(request);
    }

    @Override
    public List<AggregatedTrades> getAggregatedTrades(AggregatedTradesRequest request) throws ApiException {
        return testOrderManager.getAggregatedTrades(request);
    }

    @Override
    public List<Candlestick> getCandlestickBars(CandlesticksRequest request) throws ApiException {
        return defaultApi.getCandlestickBars(request);
    }

    @Override
    public ExchangeInfo getExchangeInfo() throws ApiException {
        return defaultApi.getExchangeInfo();
    }

    @Override
    public List<Ticker24> get24HrPriceStatistics() throws ApiException {
        return defaultApi.get24HrPriceStatistics();
    }

    @Override
    public Ticker24 get24HrPriceStatistics(String symbol) throws ApiException {
        return defaultApi.get24HrPriceStatistics(symbol);
    }

    @Override
    public Double getPrice(String symbol) throws ApiException {
        return defaultApi.getPrice(symbol);
    }

    @Override
    public Map<String, Double> getPrices() throws ApiException {
        return defaultApi.getPrices();
    }

    @Override
    public AveragePrice getAveragePrice(String symbol) throws ApiException {
        return defaultApi.getAveragePrice(symbol);
    }

    @Override
    public List<Ticker> getBookTickers() throws ApiException {
        return defaultApi.getBookTickers();
    }

    @Override
    public Ticker getBookTicker(String symbol) throws ApiException {
        return defaultApi.getBookTicker(symbol);
    }

    @Override
    public Account getAccount() throws ApiException {
        return testAccountManager.getAccount();
    }

    @Override
    public TradeFee getTradeFee(String symbol, Integer recvWindow) throws ApiException {
        return defaultApi.getTradeFee(symbol, recvWindow);
    }

    @Override
    public List<Order> getOpenOrders() throws ApiException {
        return testOrderManager.getOpenOrders();
    }

    @Override
    public List<Order> getOpenOrders(OpenOrderRequest request) throws ApiException {
        return testOrderManager.getOpenOrders(request);
    }

    @Override
    public List<Order> cancelOpenOrder(DeleteOrderRequest request) throws ApiException {
        return testOrderManager.cancelOpenOrder(request);
    }

    @Override
    public List<Order> getOrders(AllOrderRequest request) throws ApiException {
        return testOrderManager.getOrders(request);
    }

    @Override
    public List<Order> geClosedOrders(ClosedOrderRequest request) throws ApiException {
        return testOrderManager.geClosedOrders(request);
    }

    @Override
    public List<Order> getOrders(String symbol, Long orderId, int limit) throws ApiException {
        return testOrderManager.getOrders(symbol, orderId, limit);
    }

    @Override
    public List<Trade> getMyTrades(TradesRequest request) throws ApiException {
        return testOrderManager.getMyTrades(request);
    }

    @Override
    public List<Trade> getTrades(String symbol, int limit) throws ApiException {
        return testOrderManager.getTrades(symbol, limit);
    }

    @Override
    public Order getOrder(OrderRef orderRef) {
        return testOrderManager.getOrder(orderRef);
    }

    @Override
    public Order getOrder(OrderRequest request) throws ApiException {
        return testOrderManager.getOrder(request);
    }

    @Override
    public OrderRef createOrder(OrderPlacement orderPlacement) throws ApiException {
        return testOrderManager.createOrder(orderPlacement);
    }

    @Override
    public OrderRef createTestOrder(OrderPlacement orderPlacement) throws ApiException {
        return testOrderManager.createTestOrder(orderPlacement);
    }

    @Override
    public Order deleteOrderById(String symbol, Long orderId) throws ApiException {
        return testOrderManager.deleteOrderById(symbol, orderId);
    }

    @Override
    public Order deleteOrderByClientOrderId(String symbol, String clientOrderId) throws ApiException {
        return testOrderManager.deleteOrderByClientOrderId(symbol, clientOrderId);
    }

    @Override
    public List<FiatOrder> getFiatOrders(FiatOrderRequest request) throws ApiException {
        return depositManager.getFiatOrders(request);
    }

    @Override
    public List<FiatPayment> getFiatPayments(FiatOrderRequest request) throws ApiException {
        return depositManager.getFiatPayments(request);
    }

    @Override
    public String withdraw(WithdrawOrder withdrawOrder) throws ApiException {
        return depositManager.withdraw(withdrawOrder);
    }

    @Override
    public List<WithdrawTransaction> getWithdrawHistory(HistoryFilter historyFilter) throws ApiException {
        return depositManager.getWithdrawHistory(historyFilter);
    }

    @Override
    public List<DepositTransaction> getDepositHistory(HistoryFilter historyFilter) throws ApiException {
        return depositManager.getDepositHistory(historyFilter);
    }

    @Override
    public SystemStatus getSystemStatus() throws ApiException {
        return defaultApi.getSystemStatus();
    }

    @Override
    public void deleteUserDataStream(String listenKey) throws ApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public String startUserDataStream() throws ApiException {
        String id = UUID.randomUUID()+"-userdata";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepUserDataStream(String listenKey) throws ApiException {
        if(!sessionKeys.contains(listenKey)){
            throw new ApiException("No such listen key: " + listenKey);
        }
    }

    @Override
    public String startIsolatedMarginStream() throws ApiException {
        String id = UUID.randomUUID()+"-isolated";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepIsolatedMarginStream(String listenKey) throws ApiException {
        // ignore
    }

    @Override
    public void deleteIsolatedMarginStream(String listenKey) throws ApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public String startMarginStream() throws ApiException {
        String id = UUID.randomUUID()+"-margin";
        sessionKeys.add(id);
        return id;
    }

    @Override
    public void keepMarginStream(String listenKey) throws ApiException {
        // ignore
    }

    @Override
    public void deleteMarginStream(String listenKey) throws ApiException {
        sessionKeys.remove(listenKey);
    }

    @Override
    public Session getWebsocketSession(String url, WebSocketAdapter adapter) throws ApiException {
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

        public void pushAccountUpdate(Account account){
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
            for(Asset asset:account.getAssets().values()){
                JsonElement at = gson.toJsonTree(asset);
                b.add(at);
            }
            adapter.onWebSocketText(gson.toJson(o));
        }

        public void pushAssetUpdate(Asset asset){
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

        public void pushOrderUpdate(Order order, Trade trade){
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
            o.addProperty("r", RejectReason.NONE.toString());

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


