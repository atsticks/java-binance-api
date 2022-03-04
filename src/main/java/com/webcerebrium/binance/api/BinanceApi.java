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


public interface BinanceApi {

    void initialize();

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // GENERAL ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checking connectivity,
     * @return empty object
     * @throws BinanceApiException in case of any error
     */
    boolean ping();

    /**
     * Checking server time,
     * @return JsonObject, expected { serverTime: 00000 }
     * @throws BinanceApiException in case of any error
     */
    Long getServerTime() throws BinanceApiException;

    // - - - - - - - - - - - -  - - - - - - - - - - - -
    // INFO ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Gets runtime information about the node.
     * @return block height, current timestamp and the number of connected peers.
     * @throws BinanceApiException in case of any error
     */
    BinanceNodeInfo getNodeInfo() throws BinanceApiException;

    /**
     * Gets the list of network peers.
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    List<BinancePeer> getPeers() throws BinanceApiException;


    // - - - - - - - - - - - - - - - - - - - - - - - -
    // MARKET ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Get latest bids and ask price.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    BinanceDepth getDepth(String symbol) throws BinanceApiException;

    /**
     * Get latest bids and ask prices, with limit explicitly set.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @param limit numeric limit of results
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    BinanceDepth getDepth(String symbol, int limit) throws BinanceApiException;

    /**
     * Get the current available spot pairs.
     *
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    JsonObject getOptionInfo() throws BinanceApiException;

    /**
     * Get the current available spot tickers.
     *
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    JsonObject getSpotTickers() throws BinanceApiException;

    /**
     * Get the current available spot tickers.
     *
     * @param symbol the price symbol.
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    JsonObject getMarkPrice(String symbol) throws BinanceApiException;

    /**
     * Get the current available trading pairs.
     *
     * @param recvWindow the recvWindow
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    List<BinancePair> getIsolatedPairs(Integer recvWindow) throws BinanceApiException;

    /**
     * Get the current available trading pairs, with a limit of 500 and offset 0.
     *
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    default List<BinancePair> getIsolatedPairs() throws BinanceApiException {
        return getIsolatedPairs(null);
    }

    /**
     * Get the current available trading pairs.
     *
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    List<BinancePair> getCrossMargingPairs() throws BinanceApiException;

    /**
     * Get compressed, historical trades.
     *
     * @param request the request, not null.
     * @return list of historical trades
     * @throws BinanceApiException in case of any error
     */
    List<BinanceHistoricalTrade> getHistoricalTrades(BinanceHistoricalTradesRequest request) throws BinanceApiException;

    /**
     * Get compressed, aggregate trades and map result into BinanceAggregatedTrades type for better readability
     * Trades that fill at the time, from the same order, with the same price will have the quantity aggregated.
     * Allowed options - fromId, startTime, endTime.
     * If both startTime and endTime are sent, limit should not be sent AND the distance between startTime and endTime must be less than 24 hours.
     * If fromId, startTime, and endTime are not sent, the most recent aggregate trades will be returned.
     *
     * @param request the request, not null.
     * @return list of aggregated trades
     * @throws BinanceApiException in case of any error
     */
    List<BinanceAggregatedTrades> getAggregatedTrades(BinanceAggregatedTradesRequest request) throws BinanceApiException;

    /**
     * Kline/candlestick bars for a symbol. Klines are uniquely identified by their open time.
     * if startTime and endTime are not sent, the most recent klines are returned.
     * @param request the request, not null.
     * @return list of candlesticks
     * @throws BinanceApiException in case of any error
     */
    List<BinanceCandlestick> getCandlestickBars(CandlestickBarRequest request) throws BinanceApiException;
    /**
     * Exchange info - information about open markets
     * @return BinanceExchangeInfo
     * @throws BinanceApiException in case of any error
     */
    BinanceExchangeInfo getExchangeInfo() throws BinanceApiException;

    /**
     * 24hr ticker price change statistics
     * @return json array with prices for all symbols
     * @throws BinanceApiException in case of any error
     */
    List<BinanceTicker24> get24HrPriceStatistics() throws BinanceApiException;

    /**
     * 24hr ticker price change statistics
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return json with prices
     * @throws BinanceApiException in case of any error
     */
    BinanceTicker24 get24HrPriceStatistics(String symbol) throws BinanceApiException;


    /**
     * Get latest price for a symbol.
     *
     * @param symbol the symbol, not null.
     * @return last price.
     * @throws BinanceApiException  in case of any error
     */
    Double getPrice(String symbol) throws BinanceApiException;

    /**
     * Latest price for all symbols -
     *
     * @return Map of big decimals
     * @throws BinanceApiException in case of any error
     */
    Map<String, Double> getPrices() throws BinanceApiException;

    /**
     * Get the average price for a symbol.
     * @param symbol the symbol, not null.
     * @return the price found.
     */
    BinanceAveragePrice getAveragePrice(String symbol) throws BinanceApiException;

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @return map of BinanceTicker
     * @throws BinanceApiException in case of any error
     */
    List<BinanceTicker> getBookTickers() throws BinanceApiException;

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @param symbol the symbol, not null.
     * @return map of BinanceTicker
     * @throws BinanceApiException in case of any error
     */
    BinanceTicker getBookTicker(String symbol) throws BinanceApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // ACCOUNT READ-ONLY ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Getting account information
     * @return JsonObject
     * @throws BinanceApiException in case of any error
     */
    BinanceAccount getAccount() throws BinanceApiException;


    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param symbol the symbol, required.
     * @return the fee, or null.
     * @throws BinanceApiException in case of any error
     */
    default BinanceTradeFee getTradeFee(String symbol) throws BinanceApiException {
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
     * @throws BinanceApiException in case of any error
     */
    BinanceTradeFee getTradeFee(String symbol, Integer recvWindow) throws BinanceApiException;

    /**
	 * Get all my open orders. <strong>Can use up a lot of Binance Weight. Use with caution.</strong>
	 * <p>
	 * see https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md#current-open-orders-user_data
	 * 
	 * @return List of Orders
	 * @throws BinanceApiException in case of any error
	 */
	List<BinanceOrder> getOpenOrders() throws BinanceApiException;

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    List<BinanceOrder> getOpenOrders(BinanceOpenOrderRequest request) throws BinanceApiException;

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    List<BinanceOrder> cancelOpenOrder(BinanceDeleteOrderRequest request) throws BinanceApiException;

    /**
     * Get all my orders.
     *
     * @param request the request, not null.
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    List<BinanceOrder> getOrders(BinanceAllOrderRequest request) throws BinanceApiException;

    /**
     * Get all closed orders.
     *
     * @param request, required
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    List<BinanceOrder> geClosedOrders(BinanceClosedOrderRequest request) throws BinanceApiException;

    /**
     * Get all orders on a symbol; active, canceled, or filled.
     * If orderId is set (not null and greater than 0), it will get orders greater or equal than orderId.
     * Otherwise most recent orders are returned.
     * @param symbol i.e. BNBBTC
     * @param orderId numeric Order ID
     * @param limit numeric limit of orders in result
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    List<BinanceOrder> getOrders(String symbol, Long orderId, int limit) throws BinanceApiException;

    /**
     * short version of allOrders
     * @param symbol i.e. BNBBTC
     * @return list of orders
     * @throws BinanceApiException in case of any error
     */
    default List<BinanceOrder> getOrders(String symbol) throws BinanceApiException {
        return getOrders(symbol, 0L, 500);
    }

    /**
     * Get my trades for a specific account and symbol.
     *
     * @param request the request, not null.
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    List<BinanceTrade> getMyTrades(BinanceMyTradesRequest request) throws BinanceApiException;

    /**
     * Get trades for a specific symbol.
     *
     * @param symbol i.e. BNBBTC
     * @param limit numeric limit of results
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    List<BinanceTrade> getTrades(String symbol, int limit) throws BinanceApiException;

    /**
     * Get trades for a specific symbol, using default limit=500.
     *
     * @param symbol i.e. BNBBTC
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    default List<BinanceTrade> getTrades(String symbol) throws BinanceApiException {
        return getTrades(symbol, 500);
    }


    /**
     * Get order status and details.
     *
     * @param orderRef the order reference as returned by {@link #createOrder(BinanceOrderPlacement)} , required.
     * @return BinanceOrder object if successfull
     * @throws BinanceApiException in case of any error
     */
    BinanceOrder getOrder(BinanceOrderRef orderRef)throws BinanceApiException;


    /**
     * Get order status and details.
     *
     * @param request the request, required.
     * @return BinanceOrder object if successfull
     * @throws BinanceApiException in case of any error
     */
    BinanceOrder getOrder(BinanceOrderRequest request) throws BinanceApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // TRADING ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    BinanceOrderRef createOrder(BinanceOrderPlacement orderPlacement)  throws BinanceApiException;

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    BinanceOrderRef createTestOrder(BinanceOrderPlacement orderPlacement)  throws BinanceApiException;

    /**
     * Deletes order by order ID
     * @param symbol i.e. "BNBBTC"
     * @param orderId numeric Order ID
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    BinanceOrder deleteOrderById(String symbol, Long orderId) throws BinanceApiException;

    /**
     * Deletes order by original client ID
     * @param symbol i.e. "BNBBTC"
     * @param origClientOrderId string order ID, generated by client
     * @return json result
     * @throws BinanceApiException in case of any error
     */
    BinanceOrder deleteOrderByClientId(String symbol, String origClientOrderId) throws BinanceApiException;

    /**`
     * Deletes order by BinanceOrder object
     * @param order object of existing order
     * @return json result
     * @throws BinanceApiException in case of any error
     */
    default BinanceOrder deleteOrder(BinanceOrder order) throws BinanceApiException {
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
     * @throws BinanceApiException in case of any error
     */
    String startUserDataStream() throws BinanceApiException;

    /**
     * Keep user data stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    void keepUserDataStream(String listenKey) throws BinanceApiException;

    /**
     * Close user data stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    void deleteUserDataStream(String listenKey) throws BinanceApiException;

    /**
     * Start a new isolated margin stream. The stream will close after 60 minutes unless a keepalive is sent.
     * If the account has an active listenKey, that listenKey will be returned and its validity will be
     * extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    String startIsolatedMarginStream() throws BinanceApiException;

    /**
     * Keep isolated margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    void keepIsolatedMarginStream(String listenKey) throws BinanceApiException;

    /**
     * Close isolated margin data stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    void deleteIsolatedMarginStream(String listenKey) throws BinanceApiException;

    /**
     * Start a new margin stream. The stream will close after 60 minutes unless a keepalive is sent. If the account has an active listenKey, that listenKey will be returned and its validity will be extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    String startMarginStream() throws BinanceApiException;

    /**
     * Keep user margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    void keepMarginStream(String listenKey) throws BinanceApiException;

    /**
     * Close margin stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    void deleteMarginStream(String listenKey) throws BinanceApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // FIAT ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws BinanceApiException in case of any error
     * @return a list of Fiat orders.
     */
    List<BinanceFiatOrder> getFiatOrders(BinanceFiatOrderRequest request) throws BinanceApiException;

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws BinanceApiException in case of any error
     * @return list of fiat payments.
     */
    List<BinanceFiatPayment> getFiatPayments(BinanceFiatOrderRequest request) throws BinanceApiException;

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // WEBSOCKET ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Base method for all websockets streams
     * @param url derived methods will use unique base url
     * @param adapter  class to handle the event
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    Session getWebsocketSession(String url, WebSocketAdapter adapter) throws BinanceApiException;

    /**
     * Depth Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the event
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketDepth(String symbol, BinanceWebSocketAdapterDepth adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 20 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketDepth20(String symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth20", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 10 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter  class to handle the events
     * @return  web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketDepth10(String symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth10", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 5 lavels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketDepth5(String symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@depth5", adapter);
    }

    /**
     * Klines Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param interval  valid time interval, see BinanceInterval enum
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketKlines(String symbol, BinanceInterval interval, BinanceWebSocketAdapterKline adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@kline_" + interval.toString(), adapter);
    }

    /**
     * Trades Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocketTrades(String symbol, BinanceWebSocketAdapterAggTrades adapter) throws BinanceApiException {
        return getWebsocketSession(Objects.requireNonNull(symbol).toLowerCase() + "@aggTrade", adapter);
    }

    /**
     * User Data Websocket Stream Listener
     * @param listenKey string, received in startUserDataStream()
     * @param adapter class to handle the event
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    default Session websocket(String listenKey, BinanceWebSocketAdapterUserData adapter) throws BinanceApiException {
        return getWebsocketSession(listenKey, adapter);
    }

    /**
     * Withdrawal APIs.W
     *
     * @param withdrawOrder withdrawOrder
     * @return withdraw id
     * @throws BinanceApiException in case of any error
     */
    String withdraw(BinanceWithdrawOrder withdrawOrder) throws BinanceApiException;

    /**
     * Getting history of withdrawals.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws BinanceApiException in case of any error
     */
    List<BinanceWithdrawTransaction> getWithdrawHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException;

    /**
     * Getting history of deposits.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws BinanceApiException in case of any error
     */
    List<BinanceDepositTransaction> getDepositHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException;

    /**
    * Getting status of the system.
    * @return Temporary returns JsonObject
    * @throws BinanceApiException in case of any error
    */
    BinanceSystemStatus getSystemStatus() throws BinanceApiException;

}


