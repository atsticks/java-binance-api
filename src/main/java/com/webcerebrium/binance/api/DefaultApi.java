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
public class DefaultApi implements Api {

    /* Actual API key and Secret Key that will be used */
    public String apiKey;
    public String secretKey;
    public Integer connectionTimeoutSeconds;
    private RateLimiter limiter = new RateLimiter(1200, TimeUnit.MINUTES, 1);

    /**
     * API Base URL
     */
    public String baseUrl = "https://api.binance.com/api/";

    /**
     * API Base URL
     */
    public String baseTestUrl = "https://testnet.binance.vision/api/";
    /**
     * Old V-API Base URL. Might not function well at that moment, please use modern wapi3 API instead.
     */
    public String baseVapiUrl = "https://api.binance.com/vapi/";
    /**
     * Old W-API Base URL. Might not function well at that moment, please use modern wapi3 API instead.
     */
    public String baseWapiUrl = "https://api.binance.com/wapi/";
    /**
     * Old W-API Base URL. Might not function well at that moment, please use modern wapi3 API instead.
     */
    public String baseSapiUrl = "https://api.binance.com/sapi/";
    /**
     * W-API3 Base URL.
     */
    public String baseWapi3 = "https://api.binance.com/wapi/v3";
    /**
     * Base URL for websockets
     */
    public String websocketBaseUrl = "wss://stream.binance.com:9443/ws/";

    /**
     * Guava Class Instance for escaping
     */
    private Escaper esc = UrlEscapers.urlFormParameterEscaper();

    private Semaphore maxConnections = new Semaphore(10);

    /**
     * Constructor of API when you exactly know the keys
     * @param apiKey Public API Key
     * @param secretKey Secret API Key
     * @throws ApiException in case of any error
     */
    public DefaultApi(String apiKey, String secretKey) throws ApiException {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        validateCredentials();
    }

    /**
     * Constructor of API - keys are loaded from VM options, environment variables, resource files
     */
    public DefaultApi() {
        BinanceConfig config = new BinanceConfig();
        this.apiKey = config.getVariable("BINANCE_API_KEY");
        this.secretKey = config.getVariable("BINANCE_SECRET_KEY");
    }

    public void initialize(){
        // nothing todo here...
    }

    /**
     * Validation we have API keys set up
     * @throws ApiException in case of any error
     */
    protected void validateCredentials() throws ApiException {
        String humanMessage = "Please check environment variables or VM options";
        if (Strings.isNullOrEmpty(this.getApiKey()))
            throw new ApiException("Missing BINANCE_API_KEY. " + humanMessage);
        if (Strings.isNullOrEmpty(this.getSecretKey()))
            throw new ApiException("Missing BINANCE_SECRET_KEY. " + humanMessage);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // GENERAL ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checking connectivity,
     * @return empty object
     * @throws ApiException in case of any error
     */
    public boolean ping() {
        try {
            maxConnections.acquire();
            limiter.acquire();
            new WebRequest(baseUrl + "v1/ping")
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            return true;
        }catch(Exception e){
            log.error("Error PING: ", e);
            return false;
        }finally{
            maxConnections.release();
            limiter.release();
        }
    }

    /**
     * Checking server time,
     * @return JsonObject, expected { serverTime: 00000 }
     * @throws ApiException in case of any error
     */
    public Long getServerTime() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire();
            return (new WebRequest(baseUrl + "v1/time"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject().get("serverTime").getAsLong();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }
        finally{
            maxConnections.release();
            limiter.release();
        }
    }

    // - - - - - - - - - - - -  - - - - - - - - - - - -
    // INFO ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Gets runtime information about the node.
     * @return block height, current timestamp and the number of connected peers.
     * @throws ApiException in case of any error
     */
    public NodeInfos getNodeInfo() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire();
            JsonObject ob = new WebRequest(baseUrl + "v1/node-info")
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            JsonObject node_info = ob.get("node_info").getAsJsonObject();
            JsonObject sync_info = ob.get("sync_info").getAsJsonObject();
            JsonObject validator_info = ob.get("validator_info").getAsJsonObject();
            NodeInfos nodeInfo = new NodeInfos();
            nodeInfo.setNodeInfo(new NodeInfos.NodeInfo(node_info));
            nodeInfo.setSyncInfo(new NodeInfos.SyncInfo(sync_info));
            nodeInfo.setValidatorInfo(new NodeInfos.ValidatorInfo(validator_info));
            return nodeInfo;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }
        finally{
            maxConnections.release();
            limiter.release();
        }
    }

    /**
     * Gets the list of network peers.
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public List<Peer> getPeers() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire();
            JsonArray arr = new WebRequest(baseUrl + "v1/peers")
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonArray();
            List<Peer> peers = new ArrayList<>();
            for (JsonElement p : arr) {
                peers.add(new Peer(p.getAsJsonObject()));
            }
            return peers;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release();
        }
    }


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
    public Depth getDepth(String symbol) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire();
            JsonObject ob = new WebRequest(baseUrl + "v1/depth?symbol=" + Objects.requireNonNull(symbol))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            JsonArray asks = ob.get("asks").getAsJsonArray();
            JsonArray bids = ob.get("bids").getAsJsonArray();
            return new Depth(symbol,
                    // Price and qty in decimal form, e.g. 1.00000000
                    new BidOrAsk(BidType.ASK, asks),
                    new BidOrAsk(BidType.BID, bids));
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release();
        }
    }

    /**
     * Get latest bids and ask prices, with limit explicitly set.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @param limit numeric limit of results
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public Depth getDepth(String symbol, int limit) throws ApiException {
        int weight = limit/100;
        if(weight==0)
            weight = 1;
        try{
            maxConnections.acquire();
            limiter.acquire(weight);
            JsonObject ob = new WebRequest(baseUrl + "v1/depth?symbol=" + Objects.requireNonNull(symbol) + "&limit=" + limit)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            JsonArray asks = ob.get("asks").getAsJsonArray();
            JsonArray bids = ob.get("bids").getAsJsonArray();
            return new Depth(symbol,
                    // Price and qty in decimal form, e.g. 1.00000000
                    new BidOrAsk(BidType.ASK, asks),
                    new BidOrAsk(BidType.BID, bids));
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(weight);
        }
    }

    /**
     * Get the current available spot pairs.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public JsonObject getOptionInfo() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String url = baseVapiUrl + "v1/optionInfo";
            JsonObject obj = new WebRequest(url).sign(apiKey, secretKey, null)
                    .read().asJsonObject();
            List<MarketPair> pairs = new ArrayList<>();
//            arr.forEach(p -> {
//                pairs.add(new BinancePair(p.getAsJsonObject(), BinancePair.BinancePairType.isolated));
//            });
            return obj;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get the current available spot tickers.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public JsonObject getSpotTickers() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String url = baseVapiUrl + "v1/ticker";
            JsonObject obj = new WebRequest(url).sign(apiKey, secretKey, null)
                    .read().asJsonObject();
            List<MarketPair> pairs = new ArrayList<>();
//            arr.forEach(p -> {
//                pairs.add(new BinancePair(p.getAsJsonObject(), BinancePair.BinancePairType.isolated));
//            });
            return obj;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get the current available spot tickers.
     *
     * @param symbol the price symbol.
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public JsonObject getMarkPrice(String symbol) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String url = baseVapiUrl + "v1/mark";
            if(symbol!=null){
                url += "&symbol="+symbol;
            }
            JsonObject obj = new WebRequest(url).sign(apiKey, secretKey, null)
                    .read().asJsonObject();
            List<MarketPair> pairs = new ArrayList<>();
//            arr.forEach(p -> {
//                pairs.add(new BinancePair(p.getAsJsonObject(), BinancePair.BinancePairType.isolated));
//            });
            return obj;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get the current available trading pairs.
     *
     * @param recvWindow the recvWindow
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public List<MarketPair> getIsolatedPairs(Integer recvWindow) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            String url = baseSapiUrl + "v1/margin/isolated/allPairs";
            if(recvWindow!=null){
                url += "?recvWindow="+recvWindow;
            }
            JsonArray arr = new WebRequest(url).sign(apiKey, secretKey, null)
                    .read().asJsonArray();
            List<MarketPair> pairs = new ArrayList<>();
            arr.forEach(p -> {
                pairs.add(new MarketPair(p.getAsJsonObject(), MarketPair.PairType.isolated));
            });
            return pairs;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get the current available trading pairs.
     *
     * @return result in JSON
     * @throws ApiException in case of any error
     */
    public List<MarketPair> getCrossMargingPairs() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonArray arr = new WebRequest( baseSapiUrl + "v1/margin/allPairs" )
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey, secretKey, null)
                    .read().asJsonArray();
            List<MarketPair> pairs = new ArrayList<>();
            arr.forEach(p -> {
                pairs.add(new MarketPair(p.getAsJsonObject(), MarketPair.PairType.crossmargin));
            });
            return pairs;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Get compressed, historical trades.
     *
     * @param request the request, not null.
     * @return list of historical trades
     * @throws ApiException in case of any error
     */
    public List<HistoricalTrade> getHistoricalTrades(HistoricalTradesRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(5);
            String u = baseUrl + "v3/historicalTrades" + request.toQueryString();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).read().getLastResponse();
            Type listType = new TypeToken<List<HistoricalTrade>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(5);
        }
    }

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
    public List<AggregatedTrades> getAggregatedTrades(AggregatedTradesRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseUrl + "v3/aggTrades" + request.toQueryString();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).read().getLastResponse();
            Type listType = new TypeToken<List<AggregatedTrades>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Kline/candlestick bars for a symbol. Klines are uniquely identified by their open time.
     * if startTime and endTime are not sent, the most recent klines are returned.
     * @param request the request, not null.
     * @return list of candlesticks
     * @throws ApiException in case of any error
     */
    public List<Candlestick> getCandlestickBars(CandlesticksRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire();
            String u = baseUrl + "v3/klines" +request.toQueryString();
            JsonArray jsonElements = new WebRequest(u).connectionTimeoutSeconds(connectionTimeoutSeconds).read().asJsonArray();
            List<Candlestick> list = new LinkedList<>();
            for (JsonElement e : jsonElements) list.add(new Candlestick(request.getSymbol(), request.getInterval())
                    .read(e.getAsJsonArray(), request.getInterval()));
            return list;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release();
        }
    }

    /**
     * Exchange info - information about open markets
     * @return BinanceExchangeInfo
     * @throws ApiException in case of any error
     */
    public ExchangeInfo getExchangeInfo() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            JsonObject jsonObject = (new WebRequest(baseUrl + "v3/exchangeInfo"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            return new ExchangeInfo(jsonObject);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * 24hr ticker price change statistics
     * @return json array with prices for all symbols
     * @throws ApiException in case of any error
     */
    public List<Ticker24> get24HrPriceStatistics() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(40);
            List<Ticker24> result = new ArrayList<>();
            JsonArray data = new WebRequest(baseUrl + "v1/ticker/24hr" )
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonArray();
            data.forEach(d -> {
                Ticker24 ticker = new Ticker24();
                ticker.setSymbol(d.getAsJsonObject().get("symbol").getAsString());
                ticker.read(d.getAsJsonObject());
                result.add(ticker);
            });
            return result;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(40);
        }
    }

    /**
     * 24hr ticker price change statistics
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return json with prices
     * @throws ApiException in case of any error
     */
    public Ticker24 get24HrPriceStatistics(String symbol) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            Ticker24 ticker = new Ticker24();
            ticker.setSymbol(Objects.requireNonNull(symbol));
            ticker.read (new WebRequest(baseUrl + "v1/ticker/24hr?symbol=" + symbol)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject());
            return ticker;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }


    /**
     * Get latest price for a symbol.
     *
     * @param symbol the symbol, not null.
     * @return last price.
     * @throws ApiException  in case of any error
     */
    public Double getPrice(String symbol) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject ob = new WebRequest(baseUrl + "v3/ticker/price?symbol="+Objects.requireNonNull(symbol))
                    .read().asJsonObject();
            return ob.get("price").getAsDouble();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Latest price for all symbols -
     *
     * @return Map of big decimals
     * @throws ApiException in case of any error
     */
    public Map<String, Double> getPrices() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            Map<String, Double> map = new ConcurrentHashMap<>();
            JsonArray array = (new WebRequest(baseUrl + "v3/ticker/price"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonArray();
            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                map.put(obj.get("symbol").getAsString(), obj.get("price").getAsDouble());
            }
            return map;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }

    /**
     * Get the average price for a symbol.
     * @param symbol the symbol, not null.
     * @return the price found.
     */
    public AveragePrice getAveragePrice(String symbol) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject ob = new WebRequest(baseUrl + "v3/avgPrice?symbol="+Objects.requireNonNull(symbol))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            AveragePrice price = new AveragePrice(symbol);
            price.read(ob);
            return price;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @return map of BinanceTicker
     * @throws ApiException in case of any error
     */
    public List<Ticker> getBookTickers() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String lastResponse = (new WebRequest(baseUrl + "v3/ticker/bookTicker"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).read().getLastResponse();
            Type listType = new TypeToken<List<Ticker>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @param symbol the symbol, not null.
     * @return map of BinanceTicker
     * @throws ApiException in case of any error
     */
    public Ticker getBookTicker(String symbol) throws ApiException {
        try {
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject ob = new WebRequest(baseUrl + "v3/ticker/bookTicker?symbol=" + Objects.requireNonNull(symbol))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).read().asJsonObject();
            Ticker ticker = new Ticker(symbol);
            ticker.read(ob);
            return ticker;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // ACCOUNT READ-ONLY ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Getting account information
     * @return JsonObject
     * @throws ApiException in case of any error
     */
    public Account getAccount() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            Account account = new Account();
            account.read (new WebRequest(baseUrl + "v3/account")
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey, secretKey, null).read().asJsonObject());
            return account;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }


    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param symbol the symbol, required.
     * @return the fee, or null.
     * @throws ApiException in case of any error
     */
    public TradeFee getTradeFee(String symbol) throws ApiException {
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
    public TradeFee getTradeFee(String symbol, Integer recvWindow) throws ApiException {
        try {
            maxConnections.acquire();
            limiter.acquire(1);
            String url = baseSapiUrl + "v1/asset/tradeFee?symbol=" + Objects.requireNonNull(symbol);
            if (recvWindow != null) {
                url += "&recvWindow=" + recvWindow;
            }
            JsonArray arr = new WebRequest(url)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey, secretKey, null).read().asJsonArray();
            for (JsonElement tr : arr) {
                TradeFee fee = new TradeFee();
                fee.setSymbol(symbol);
                fee.setTimestamp(System.currentTimeMillis());
                fee.setMakerCommission(tr.getAsJsonObject().get("makerCommission").getAsDouble());
                fee.setTakerCommission(tr.getAsJsonObject().get("takerCommission").getAsDouble());
                return fee;
            }
            return null;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

//    /**
//     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
//     * Transfer and other transaction fees are not included. Order by block height DESC. Query
//     * Window: Default query window is latest 7 days; The maximum start - end query window is
//     * 3 months. Rate Limit: 5 requests per IP per second.
//     * @param request the request, required.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getExchangeFees(BinanceExchangeFeeRequest request) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v1/block-exchange-fee"+request.toQueryString()))
//                .sign(apiKey, secretKey, null).read().asJsonArray();
//    }
//
//    /**
//     * Getting account time locks of an address.
//     * @param address the address, required.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonObject getTimeLocks(String address) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v3/timelocks/"+address+"?id="))
//                .sign(apiKey, secretKey, null).read().asJsonObject();
//    }
//
//    /**
//     * Getting account time locks of an address.
//     * @param address the address, required.
//     * @param recordId the record id, optional.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonObject getTimeLock(String address, Integer recordId) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v3/timelock/"+address+"?id="+recordId))
//                .sign(apiKey, secretKey, null).read().asJsonObject();
//    }
//
//    /**
//     * Get a list of tokens that have been issued.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getTokens() throws BinanceApiException {
//        return getTokens(null, null);
//    }
//
//    /**
//     * Get a list of tokens that have been issued.
//     * @param limit the limit, default 100.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getTokens(Integer limit) throws BinanceApiException {
//        return getTokens(limit, null);
//    }
//
//    /**
//     * Get a list of tokens that have been issued.
//     * @param limit the limit, default 100.
//     * @param offset the offset, default 0.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getTokens(Integer limit, Integer offset) throws BinanceApiException {
//        if(limit==null){
//            limit = 100;
//        }
//        if(offset==null){
//            offset = 0;
//        }
//        return (new BinanceRequest(baseUrl + "v3/tokens?limit"+limit+"&offset="+offset))
//                .sign(apiKey, secretKey, null).read().asJsonArray();
//    }
//
//    /**
//     * Gets transaction metadata by transaction ID. By default, transactions are returned
//     * in a raw format.
//     * @param transactionId the transaction id, required.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonObject getTransaction(String transactionId) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v1/tx/"+transactionId+"?format=json"))
//                .sign(apiKey, secretKey, null).read().asJsonObject();
//    }
//
//    /**
//     * Gets a list of transactions. Multisend transaction is not available in this API. Currently 'confirmBlocks' and 'txAge' are not supported.
//     *
//     * Query Window: Default query window is latest 24 hours; The maximum start - end query window is 3 months.
//     *
//     * Rate Limit: 60 requests per IP per minute.
//     * @param request the request, required.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getTransactions(BinanceTransactionRequest request) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v1/transactions"+request.toQueryString()+"&format=json"))
//                .sign(apiKey, secretKey, null).read().asJsonArray();
//    }
//
//    /**
//     * Getting account time locks of an address.
//     * @param address the address, required.
//     * @param id the record id of timelock to query
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonObject getTimeLocks(String address, long id) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v3/timelocks/"+address+"?id="+id))
//                .sign(apiKey, secretKey, null).read().asJsonObject();
//    }

    /**
	 * Get all my open orders. <strong>Can use up a lot of Binance Weight. Use with caution.</strong>
	 * <p>
	 * see https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md#current-open-orders-user_data
	 *
	 * @return List of Orders
	 * @throws ApiException in case of any error
	 */
	public List<Order> getOpenOrders() throws ApiException {
	    try{
            maxConnections.acquire();
	        limiter.acquire(3);
            String u = baseUrl + "v3/openOrders";
            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(3);
        }
	}

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    public List<Order> getOpenOrders(OpenOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(3);
            String u = baseUrl + "v3/openOrders" + request.toQueryString();
            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(3);
        }
    }

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    public List<Order> cancelOpenOrder(DeleteOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(3);
            String u = baseUrl + "v3/openOrders" + request.toQueryString();
            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).delete().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(3);
        }
    }

    /**
     * Get all my orders.
     *
     * @param request the request, not null.
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    public List<Order> getOrders(AllOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            String u = baseUrl + "v3/allOrders"+request.toQueryString();
            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get all closed orders.
     *
     * @param request, required
     * @return List of Orders
     * @throws ApiException in case of any error
     */
    public List<Order> geClosedOrders(ClosedOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/closedOrders" + request.toQueryString();
            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }
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
    public List<Order> getOrders(String symbol, Long orderId, int limit) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            String u = baseUrl + "v3/allOrders?symbol=" + Objects.requireNonNull(symbol) + "&limit=" + limit;
            if (orderId != null && orderId > 0) u += "&orderId=" + orderId;

            String lastResponse = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Order>>() {}.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get my trades for a specific account and symbol.
     *
     * @param request the request, not null.
     * @return list of trades
     * @throws ApiException in case of any error
     */
    public List<Trade> getMyTrades(TradesRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(10);
            String u = baseUrl + "v3/myTrades" + request.toQueryString();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            Type listType = new TypeToken<List<Trade>>() {}.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(10);
        }
    }

    /**
     * Get trades for a specific symbol.
     *
     * @param symbol i.e. BNBBTC
     * @param limit numeric limit of results
     * @return list of trades
     * @throws ApiException in case of any error
     */
    public List<Trade> getTrades(String symbol, int limit) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseUrl + "v3/trades?symbol=" + Objects.requireNonNull(symbol) + "&limit=" + limit;
            // sign(apiKey, secretKey, null)
            String lastResponse = new WebRequest(u).connectionTimeoutSeconds(connectionTimeoutSeconds).read().getLastResponse();
            Type listType = new TypeToken<List<Trade>>() {}.getType();
            return new Gson().fromJson(lastResponse, listType);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }


    /**
     * Get order status and details.
     *
     * @param orderRef the order reference as returned by {@link #createOrder(OrderPlacement)} , required.
     * @return BinanceOrder object if successfull
     * @throws ApiException in case of any error
     */
    public Order getOrder(OrderRef orderRef){
        if(orderRef.isTest()){
            Order order = new Order();
            order.setOrderId(orderRef.getOrderId());
            order.setSymbol(orderRef.getSymbol());
            order.setClientOrderId(orderRef.getClientOrderId());
            order.setPrice(order.getPrice());
            order.setTime(orderRef.getTransactTime());
            order.setStatus(OrderStatus.NEW);
            if(orderRef.getPlacement()!=null) {
                order.setTimeInForce(orderRef.getPlacement().getTimeInForce());
                order.setIcebergQty(orderRef.getPlacement().getIcebergQty());
                order.setStopPrice(orderRef.getPlacement().getStopPrice());
                order.setOrigQty(orderRef.getPlacement().getQuantity());
                order.setExecutedQty(orderRef.getPlacement().getQuantity());
                order.setSide(orderRef.getPlacement().getSide());
                order.setType(orderRef.getPlacement().getType());
            }else{
                order.setTimeInForce(TimeInForce.GTC);
                order.setType(OrderType.MARKET);
            }
            return order;
        }
        OrderRequest request = OrderRequest.builder()
                .orderId(orderRef.getOrderId())
                .symbol(orderRef.getSymbol())
                .build();
        return getOrder(request);
    }


    /**
     * Get order status and details.
     *
     * @param request the request, required.
     * @return BinanceOrder object if successfull
     * @throws ApiException in case of any error
     */
    public Order getOrder(OrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/order"+request.toQueryString();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).read().getLastResponse();
            return (new Gson()).fromJson(lastResponse, Order.class);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // TRADING ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    public OrderRef createOrder(OrderPlacement orderPlacement)  throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/order?" + orderPlacement.getAsQuery();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).post().read().getLastResponse();
            OrderRef newOrder = (new Gson()).fromJson(lastResponse, OrderRef.class);
            newOrder.setPlacement(orderPlacement);
            return newOrder;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    public OrderRef createTestOrder(OrderPlacement orderPlacement)  throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseUrl + "v3/order/test?" + orderPlacement.getAsQuery();
            String lastResponse = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).post().read().getLastResponse();
            OrderRef newOrder;
            if(lastResponse.equals("{}")){
                newOrder = new OrderRef();
                newOrder.setPlacement(orderPlacement);
                newOrder.setOrderId(System.currentTimeMillis());
                newOrder.setTest(true);
                newOrder.setClientOrderId(orderPlacement.getNewClientOrderId());
                newOrder.setSymbol(orderPlacement.getSymbol());
                newOrder.setTransactTime(System.currentTimeMillis());
                return newOrder;
            }
            newOrder = (new Gson()).fromJson(lastResponse, OrderRef.class);
            newOrder.setPlacement(orderPlacement);
            return newOrder;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Deletes order by order ID
     * @param symbol i.e. "BNBBTC"
     * @param orderId numeric Order ID
     * @return json result from order placement
     * @throws ApiException in case of any error
     */
    public Order deleteOrderById(String symbol, Long orderId) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/order?symbol=" + Objects.requireNonNull(symbol) + "&orderId=" + orderId;
            JsonObject ob = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).delete().read().asJsonObject();
            return new Order(ob);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }
    /**
     * Deletes order by original client ID
     * @param symbol i.e. "BNBBTC"
     * @param origClientOrderId string order ID, generated by client
     * @return json result
     * @throws ApiException in case of any error
     */
    public Order deleteOrderByOrigClientId(String symbol, String origClientOrderId) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/order?symbol=" + Objects.requireNonNull(symbol) + "&origClientOrderId=" + esc.escape(origClientOrderId);
            JsonObject ob = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).delete().read().asJsonObject();
            return new Order(ob);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
    }

    /**
     * Deletes order by new client ID
     * @param symbol i.e. "BNBBTC"
     * @param clientOrderId string order ID, generated by server
     * @return json result
     * @throws ApiException in case of any error
     */
    public Order deleteOrderByClientOrderId(String symbol, String clientOrderId ) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(2);
            String u = baseUrl + "v3/order?symbol=" + Objects.requireNonNull(symbol) + "&newClientOrderId=" + esc.escape(clientOrderId);
            JsonObject ob = (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey, secretKey, null).delete().read().asJsonObject();
            return new Order(ob);
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(2);
        }
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
    public String startUserDataStream() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject jsonObject = (new WebRequest(baseUrl + "v3/userDataStream"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).post().read().asJsonObject();
            return jsonObject.get("listenKey").getAsString();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Keep user data stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    public void keepUserDataStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseUrl + "v3/userDataStream?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                        .sign(apiKey).put().read().asJsonObject();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Close user data stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    public void deleteUserDataStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseUrl + "v3/userDataStream?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).delete().read();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Start a new isolated margin stream. The stream will close after 60 minutes unless a keepalive is sent.
     * If the account has an active listenKey, that listenKey will be returned and its validity will be
     * extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    public String startIsolatedMarginStream() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject jsonObject = (new WebRequest(baseSapiUrl + "v1/userDataStream/isolated"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).post().read().asJsonObject();
            return jsonObject.get("listenKey").getAsString();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Keep isolated margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    public void keepIsolatedMarginStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseSapiUrl + "v1/userDataStream/isolated?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).put().read().asJsonObject();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Close isolated margin data stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    public void deleteIsolatedMarginStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseSapiUrl + "v1/userDataStream/isolated?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).delete().read();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Start a new margin stream. The stream will close after 60 minutes unless a keepalive is sent. If the account has an active listenKey, that listenKey will be returned and its validity will be extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    public String startMarginStream() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject jsonObject = (new WebRequest(baseSapiUrl + "v1/userDataStream"))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).post().read().asJsonObject();
            return jsonObject.get("listenKey").getAsString();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Keep user margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws ApiException in case of any error
     */
    public void keepMarginStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseSapiUrl + "v1/userDataStream?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).put().read().asJsonObject();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Close margin stream
     * @param listenKey key for user stream management
     * @throws ApiException in case of any error
     */
    public void deleteMarginStream(String listenKey) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            new WebRequest(baseSapiUrl + "v1/userDataStream?listenKey=" + esc.escape(listenKey))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).delete().read();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

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
    public List<FiatOrder> getFiatOrders(FiatOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject ob = new WebRequest(baseSapiUrl + "v1/fiat/orders"+request.toQueryString())
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey, secretKey, null).read().asJsonObject();
            if(ob.has("data")){
                JsonArray arr = ob.get("data").getAsJsonArray();
                Type listType = new TypeToken<List<FiatOrder>>() {}.getType();
                return new Gson().fromJson(arr, listType);
            }
            return Collections.emptyList();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws ApiException in case of any error
     * @return list of fiat payments.
     */
    public List<FiatPayment> getFiatPayments(FiatOrderRequest request) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            JsonObject ob = new WebRequest(baseSapiUrl + "v1/fiat/payments"+request.toQueryString())
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey, secretKey, null).read().asJsonObject();
            if(ob.has("data")){
                JsonArray arr = ob.get("data").getAsJsonArray();
                Type listType = new TypeToken<List<FiatPayment>>() {}.getType();
                return new Gson().fromJson(arr, listType);
            }
            return Collections.emptyList();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

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
    public Session getWebsocketSession(String url, WebSocketAdapter adapter) throws ApiException {
        try {
            maxConnections.acquire();
            limiter.acquire();
            URI uri = new URI(websocketBaseUrl + url);
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setTrustAll(true); // The magic
            WebSocketClient client = new WebSocketClient(sslContextFactory);
            client.start();
            return client.connect(adapter, uri).get();
        } catch (URISyntaxException e) {
            throw new ApiException("URL Syntax error: " + e.getMessage());
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }catch (Throwable e) {
            throw new ApiException("Websocket error: " + e.getMessage());
        }finally{
            maxConnections.release();
            limiter.release();
        }
    }

    /**
     * Withdrawal APIs.W
     *
     * @param withdrawOrder withdrawOrder
     * @return withdraw id
     * @throws ApiException in case of any error
     */
    public String withdraw(WithdrawOrder withdrawOrder) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseSapiUrl + "/v1/capital/withdraw/apply" + withdrawOrder.toQueryString();
            return (new WebRequest(u))
                    .connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).post().read().asJsonObject().get("id").getAsString();
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Getting history of withdrawals.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws ApiException in case of any error
     */
    public List<WithdrawTransaction> getWithdrawHistory(HistoryFilter historyFilter) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseSapiUrl + "v1/capital/withdraw/history" + historyFilter.getAsQuery();
            List<WithdrawTransaction> result = new ArrayList<>();
            JsonArray array = new WebRequest(u)
                    .connectionTimeoutSeconds(connectionTimeoutSeconds).sign(apiKey).read().asJsonArray();
            array.forEach(el -> {
                JsonObject ob = el.getAsJsonObject();
                WithdrawTransaction tx = new WithdrawTransaction();
                tx.setAddress(ob.get("address").getAsString());
                tx.setApplyTime(ob.get("applyTime").getAsString());
                tx.setAmount(ob.get("amount").getAsDouble());
                tx.setCoin(ob.get("coin").getAsString());
                tx.setId(ob.get("id").getAsString());
                tx.setNetwork(ob.get("network").getAsString());
                tx.setWithdrawOrderId(ob.get("withdrawOrderId").getAsString());
                tx.setStatus(ob.get("status").getAsInt());
                tx.setTxId(ob.get("txId").getAsString());
                tx.setTransferType(ob.get("transferType").getAsInt());
                tx.setConfirmNo(ob.get("confirmNo").getAsInt());
                tx.setTransactionFee(ob.get("transactionFee").getAsDouble());
                result.add(tx);
            });
            return result;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
     * Getting history of deposits.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws ApiException in case of any error
     */
    public List<DepositTransaction> getDepositHistory(HistoryFilter historyFilter) throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseSapiUrl + "v1/capital/deposit/hisrec" + historyFilter.getAsQuery();
            List<DepositTransaction> result = new ArrayList<>();
            JsonArray array = new WebRequest(u).connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .sign(apiKey).read().asJsonArray();
            array.forEach(el -> {
                JsonObject ob = el.getAsJsonObject();
                DepositTransaction tx = new DepositTransaction();
                tx.setAddress(ob.get("address").getAsString());
                tx.setAddressTag(ob.get("addressTag").getAsString());
                tx.setAmount(ob.get("amount").getAsDouble());
                tx.setCoin(ob.get("coin").getAsString());
                tx.setConfirmTimes(ob.get("confirmTimes").getAsString());
                tx.setNetwork(ob.get("network").getAsString());
                tx.setInsertTime(ob.get("insertTime").getAsLong());
                tx.setStatus(ob.get("status").getAsInt());
                tx.setTxId(ob.get("txId").getAsString());
                tx.setTransferType(ob.get("transferType").getAsInt());
                tx.setUnlockConfirm(ob.get("unlockConfirm").getAsString());
                result.add(tx);
            });
            return result;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }

    /**
    * Getting status of the system.
    * @return Temporary returns JsonObject
    * @throws ApiException in case of any error
    */
    public SystemStatus getSystemStatus() throws ApiException {
        try{
            maxConnections.acquire();
            limiter.acquire(1);
            String u = baseSapiUrl + "v1/system/status";
            JsonObject ob = new WebRequest(u).connectionTimeoutSeconds(connectionTimeoutSeconds)
                    .read().asJsonObject();
            SystemStatus status = new SystemStatus();
            status.setStatus(ob.get("status").getAsInt());
            status.setMessage(ob.get("msg").getAsString());
            return status;
        }catch(InterruptedException e){
            throw new ApiException(e.toString());
        }finally{
            maxConnections.release();
            limiter.release(1);
        }
    }
}


