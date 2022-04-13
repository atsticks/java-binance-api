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

import com.google.gson.JsonObject;
import com.webcerebrium.binance.datatype.*;
import com.webcerebrium.binance.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.*;


public interface Api {

    void initialize();

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // GENERAL ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checking connectivity,
     * @return empty object
     * @throws ApiException in case of any error
     */
    boolean ping();

    /**
     * Checking server time,
     * @return JsonObject, expected { serverTime: 00000 }
     * @throws ApiException in case of any error
     */
    Long getServerTime() throws ApiException;

    // - - - - - - - - - - - -  - - - - - - - - - - - -
    // INFO ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Gets runtime information about the node.
     * @return block height, current timestamp and the number of connected peers.
     * @throws ApiException in case of any error
     */
    NodeInfos getNodeInfo() throws ApiException;

    /**
     * Gets the list of network peers.
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    List<Peer> getPeers() throws ApiException;


    // - - - - - - - - - - - - - - - - - - - - - - - -
    // MARKET ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Get latest bids and ask price.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    Depth getDepth(String symbol) throws ApiException;

    /**
     * Get latest bids and ask prices, with limit explicitly set.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @param limit numeric limit of results
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    Depth getDepth(String symbol, int limit) throws ApiException;

    /**
     * Get the current available spot pairs.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    JsonObject getOptionInfo() throws ApiException;

    /**
     * Get the current available spot tickers.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    JsonObject getSpotTickers() throws ApiException;

    /**
     * Get the current available spot tickers.
     *
     * @param symbol the price symbol.
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    JsonObject getMarkPrice(String symbol) throws ApiException;

    /**
     * Get the current available trading pairs.
     *
     * @param recvWindow the recvWindow
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    List<MarketPair> getIsolatedPairs(Integer recvWindow) throws ApiException;

    /**
     * Get the current available trading pairs, with a limit of 500 and offset 0.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    default List<MarketPair> getIsolatedPairs() throws ApiException {
        return getIsolatedPairs(null);
    }

    /**
     * Get the current available trading pairs.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    List<MarketPair> getCrossMargingPairs() throws ApiException;

    /**
     * Get compressed, historical trades.
     *
     * @param request the request, not null.
     * @return list of historical trades
     * @throws ApiException in case of any error
     */
    List<HistoricalTrade> getHistoricalTrades(HistoricalTradesRequest request) throws ApiException;

    /**
     * Get compressed, aggregate trades and map result into BinanceAggregatedTrades type for better readability
     * Trades that fill at the time, from the same order, with the same price will have the quantity aggregated.
     * Allowed options - fromId, startTime, endTime.
     * If both startTime and endTime are sent, limit should not be sent AND the distance between startTime and endTime must be less than 24 hours.
     * If fromId, startTime, and endTime are not sent, the most recent aggregate trades will be returned.
     *
     * @param request the request, not null.
     * @return list of aggregated trades
     * @throws ApiException in case of any error
     */
    List<AggregatedTrades> getAggregatedTrades(AggregatedTradesRequest request) throws ApiException;

    /**
     * Kline/candlestick bars for a symbol. Klines are uniquely identified by their open time.
     * if startTime and endTime are not sent, the most recent klines are returned.
     * @param request the request, not null.
     * @return list of candlesticks
     * @throws ApiException in case of any error
     */
    List<Candlestick> getCandlestickBars(CandlesticksRequest request) throws ApiException;
    /**
     * Exchange info - information about open markets
     * @return BinanceExchangeInfo
     * @throws ApiException in case of any error
     */
    ExchangeInfo getExchangeInfo() throws ApiException;

    /**
     * 24hr ticker price change statistics
     * @return json array with prices for all symbols
     * @throws ApiException in case of any error
     */
    List<Ticker24> get24HrPriceStatistics() throws ApiException;

    /**
     * 24hr ticker price change statistics
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return json with prices
     * @throws ApiException in case of any error
     */
    Ticker24 get24HrPriceStatistics(String symbol) throws ApiException;


    /**
     * Get latest price for a symbol.
     *
     * @param symbol the symbol, not null.
     * @return last price.
     * @throws ApiException  in case of any error
     */
    Double getPrice(String symbol) throws ApiException;

    /**
     * Latest price for all symbols -
     *
     * @return Map of big decimals
     * @throws ApiException in case of any error
     */
    Map<String, Double> getPrices() throws ApiException;

    /**
     * Get the average price for a symbol.
     * @param symbol the symbol, not null.
     * @return the price found.
     */
    AveragePrice getAveragePrice(String symbol) throws ApiException;

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @return map of BinanceTicker
     * @throws ApiException in case of any error
     */
    List<Ticker> getBookTickers() throws ApiException;

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @param symbol the symbol, not null.
     * @return map of BinanceTicker
     * @throws ApiException in case of any error
     */
    Ticker getBookTicker(String symbol) throws ApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // ACCOUNT READ-ONLY ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Getting account information
     * @return JsonObject
     * @throws ApiException in case of any error
     */
    Account getAccount() throws ApiException;


    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param symbol the symbol, required.
     * @return the fee, or null.
     * @throws ApiException in case of any error
     */
    default TradeFee getTradeFee(String symbol) throws ApiException {
        return getTradeFee(symbol, null);
    }

    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param symbol the symbol, required.
     * @param recvWindow revn window size, optional.
     * @return the fee, or null.
     * @throws ApiException in case of any error
     */
    TradeFee getTradeFee(String symbol, Integer recvWindow) throws ApiException;

    /**
	 * Get all my open orders. <strong>Can use up a lot of Binance Weight. Use with caution.</strong>
	 * <p>
	 * see https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md#current-open-orders-user_data
	 * 
	 * @return List of Orders
	 * @throws ApiException in case of any error
	 */
	List<Order> getOpenOrders() throws ApiException;

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    List<Order> getOpenOrders(OpenOrderRequest request) throws ApiException;

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    List<Order> cancelOpenOrder(DeleteOrderRequest request) throws ApiException;

    /**
     * Get all my orders.
     *
     * @param request the request, not null.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    List<Order> getOrders(AllOrderRequest request) throws ApiException;

    /**
     * Get all closed orders.
     *
     * @param request, required
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    List<Order> geClosedOrders(ClosedOrderRequest request) throws ApiException;

    /**
     * Get all orders on a symbol; active, canceled, or filled.
     * If orderId is set (not null and greater than 0), it will get orders greater or equal than orderId.
     * Otherwise most recent orders are returned.
     * @param symbol i.e. BNBBTC
     * @param orderId numeric Order ID
     * @param limit numeric limit of orders in result
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    List<Order> getOrders(String symbol, Long orderId, int limit) throws ApiException;

    /**
     * short version of allOrders
     * @param symbol i.e. BNBBTC
     * @return list of orders
     * @throws ApiException in case of any error
     */
    default List<Order> getOrders(String symbol) throws ApiException {
        return getOrders(symbol, 0L, 500);
    }

    /**
     * Get my trades for a specific account and symbol.
     *
     * @param request the request, not null.
     * @return list of trades
     * @throws ApiException in case of any error
     */
    List<Trade> getMyTrades(TradesRequest request) throws ApiException;

    /**
     * Get trades for a specific symbol.
     *
     * @param symbol i.e. BNBBTC
     * @param limit numeric limit of results
     * @return list of trades
     * @throws ApiException in case of any error
     */
    List<Trade> getTrades(String symbol, int limit) throws ApiException;

    /**
     * Get trades for a specific symbol, using default limit=500.
     *
     * @param symbol i.e. BNBBTC
     * @return list of trades
     * @throws ApiException in case of any error
     */
    default List<Trade> getTrades(String symbol) throws ApiException {
        return getTrades(symbol, 500);
    }


    /**
     * Get order status and details.
     *
     * @param orderRef the order reference as returned by {@link #createOrder(OrderPlacement)} , required.
     * @return BinanceOrder object if successfull
     * @throws ApiException in case of any error
     */
    Order getOrder(OrderRef orderRef)throws ApiException;


    /**
     * Get order status and details.
     *
     * @param request the request, required.
     * @return BinanceOrder object if successfull
     * @throws ApiException in case of any error
     */
    Order getOrder(OrderRequest request) throws ApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // TRADING ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    OrderRef createOrder(OrderPlacement orderPlacement)  throws ApiException;

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    OrderRef createTestOrder(OrderPlacement orderPlacement)  throws ApiException;

    /**
     * Deletes order by order ID
     * @param symbol i.e. "BNBBTC"
     * @param orderId numeric Order ID
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    Order deleteOrderById(String symbol, Long orderId) throws ApiException;

    /**
     * Deletes order by original client ID
     * @param symbol i.e. "BNBBTC"
     * @param origClientOrderId string order ID, generated by client
     * @return json result
     * @throws ApiException in case of any error
     */
    Order deleteOrderByClientOrderId(String symbol, String origClientOrderId) throws ApiException;

    /**`
     * Deletes order by BinanceOrder object
     * @param order object of existing order
     * @return json result
     * @throws ApiException in case of any error
     */
    default Order deleteOrder(Order order) throws ApiException {
        return deleteOrderById(order.getSymbol(), order.getOrderId());
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // USER DATA STREAM
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Start a new user data stream. The stream will close after 60 minutes unless a keepalive is sent. If the account has an active listenKey, that listenKey will be returned and its validity will be extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    String startUserDataStream() throws ApiException;

    /**
     * Keep user data stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    void keepUserDataStream(String listenKey) throws ApiException;

    /**
     * Close user data stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    void deleteUserDataStream(String listenKey) throws ApiException;

    /**
     * Start a new isolated margin stream. The stream will close after 60 minutes unless a keepalive is sent.
     * If the account has an active listenKey, that listenKey will be returned and its validity will be
     * extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    String startIsolatedMarginStream() throws ApiException;

    /**
     * Keep isolated margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    void keepIsolatedMarginStream(String listenKey) throws ApiException;

    /**
     * Close isolated margin data stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    void deleteIsolatedMarginStream(String listenKey) throws ApiException;

    /**
     * Start a new margin stream. The stream will close after 60 minutes unless a keepalive is sent. If the account has an active listenKey, that listenKey will be returned and its validity will be extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    String startMarginStream() throws ApiException;

    /**
     * Keep user margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    void keepMarginStream(String listenKey) throws ApiException;

    /**
     * Close margin stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    void deleteMarginStream(String listenKey) throws ApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // FIAT ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws ApiException in case of any error
     * @return a list of Fiat orders.
     */
    List<FiatOrder> getFiatOrders(FiatOrderRequest request) throws ApiException;

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws ApiException in case of any error
     * @return list of fiat payments.
     */
    List<FiatPayment> getFiatPayments(FiatOrderRequest request) throws ApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // WEBSOCKET ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Base method for all websockets streams
     * @param url derived methods will use unique base url
     * @param adapter  class to handle the event
     * @return web socket session
     * @throws ApiException in case of any error
     */
    Session getWebsocketSession(String url, WebSocketAdapter adapter) throws ApiException;

    /**
     * Depth Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the event
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketDepth(String symbol, WebSocketDepthAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 20 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketDepth20(String symbol, WebSocketDepthLevelAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth20", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 10 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter  class to handle the events
     * @return  web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketDepth10(String symbol, WebSocketDepthLevelAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth10", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 5 lavels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketDepth5(String symbol, WebSocketDepthLevelAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth5", adapter);
    }

    /**
     * Klines Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param interval  valid time interval, see BinanceInterval enum
     * @param adapter class to handle the events
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketCandlesticks(String symbol, Interval interval, WebSocketCandlesticksAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@kline_" + interval.toString(), adapter);
    }

    /**
     * Trades Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocketTrades(String symbol, WebSocketAggTradesAdapter adapter) throws ApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@aggTrade", adapter);
    }

    /**
     * User Data Websocket Stream Listener
     * @param listenKey string, received in startUserDataStream()
     * @param adapter class to handle the event
     * @return web socket session
     * @throws ApiException in case of any error
     */
    default Session websocket(String listenKey, WebSocketUserDataAdapter adapter) throws ApiException {
        return getWebsocketSession(listenKey, adapter);
    }

    /**
     * Withdrawal APIs.W
     *
     * @param withdrawOrder withdrawOrder
     * @return withdraw id
     * @throws ApiException in case of any error
     */
    String withdraw(WithdrawOrder withdrawOrder) throws ApiException;

    /**
     * Getting history of withdrawals.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws ApiException in case of any error
     */
    List<WithdrawTransaction> getWithdrawHistory(HistoryFilter historyFilter) throws ApiException;

    /**
     * Getting history of deposits.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws ApiException in case of any error
     */
    List<DepositTransaction> getDepositHistory(HistoryFilter historyFilter) throws ApiException;

    /**
    * Getting status of the system.
    * @return Temporary returns JsonObject
    * @throws ApiException in case of any error
    */
    SystemStatus getSystemStatus() throws ApiException;

}


