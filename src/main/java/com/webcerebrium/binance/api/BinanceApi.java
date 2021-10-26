package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
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

@Slf4j
@Data
public class BinanceApi {

    /* Actual API key and Secret Key that will be used */
    public String apiKey;
    public String secretKey;

    /**
     * API Base URL
     */
    public String baseUrl = "https://www.binance.com/api/";

    /**
     * API Base URL
     */
    public String baseTestUrl = "https://testnet-dex.binance.com/api/";
    /**
     * Old W-API Base URL. Might not function well at that moment, please use modern wapi3 API instead
     */
    public String baseWapiUrl = "https://www.binance.com/wapi/";
    /**
     * Old W-API Base URL. Might not function well at that moment, please use modern wapi3 API instead
     */
    public String baseSapiUrl = "https://www.binance.com/sapi/";
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

    /**
     * Constructor of API when you exactly know the keys
     * @param apiKey Public API Key
     * @param secretKey Secret API Key
     * @throws BinanceApiException in case of any error
     */
    public BinanceApi(String apiKey, String secretKey) throws BinanceApiException {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        validateCredentials();
    }

    /**
     * Constructor of API - keys are loaded from VM options, environment variables, resource files
     */
    public BinanceApi() {
        BinanceConfig config = new BinanceConfig();
        this.apiKey = config.getVariable("BINANCE_API_KEY");
        this.secretKey = config.getVariable("BINANCE_SECRET_KEY");
    }

    /**
     * Validation we have API keys set up
     * @throws BinanceApiException in case of any error
     */
    protected void validateCredentials() throws BinanceApiException {
        String humanMessage = "Please check environment variables or VM options";
        if (Strings.isNullOrEmpty(this.getApiKey()))
            throw new BinanceApiException("Missing BINANCE_API_KEY. " + humanMessage);
        if (Strings.isNullOrEmpty(this.getSecretKey()))
            throw new BinanceApiException("Missing BINANCE_SECRET_KEY. " + humanMessage);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // GENERAL ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checking connectivity,
     * @return empty object
     * @throws BinanceApiException in case of any error
     */
    public boolean ping() {
        try {
            new BinanceRequest(baseUrl + "v1/ping")
                    .read().asJsonObject();
            return true;
        }catch(BinanceApiException e){
            log.error("Error PING: ", e);
            return false;
        }
    }

    /**
     * Checking server time,
     * @return JsonObject, expected { serverTime: 00000 }
     * @throws BinanceApiException in case of any error
     */
    public Long getServerTime() throws BinanceApiException {
        return (new BinanceRequest(baseUrl + "v1/time"))
                .read().asJsonObject().get("serverTime").getAsLong();
    }

    // - - - - - - - - - - - -  - - - - - - - - - - - -
    // INFO ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

//    /**
//     * Get the current fees.
//     * @return result in JSON
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getFees() throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v1/fees"))
//                .read().asJsonArray();
//    }

    /**
     * Gets runtime information about the node.
     * @return block height, current timestamp and the number of connected peers.
     * @throws BinanceApiException in case of any error
     */
    public BinanceNodeInfo getNodeInfo() throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v1/node-info")
                .read().asJsonObject();
        JsonObject node_info = ob.get("node_info").getAsJsonObject();
        JsonObject sync_info = ob.get("sync_info").getAsJsonObject();
        JsonObject validator_info = ob.get("validator_info").getAsJsonObject();
        BinanceNodeInfo nodeInfo = new BinanceNodeInfo();
        nodeInfo.setNodeInfo(new BinanceNodeInfo.NodeInfo(node_info));
        nodeInfo.setSyncInfo(new BinanceNodeInfo.SyncInfo(sync_info));
        nodeInfo.setValidatorInfo(new BinanceNodeInfo.ValidatorInfo(validator_info));
        return nodeInfo;
    }

    /**
     * Gets the list of network peers.
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    public List<BinancePeer> getPeers() throws BinanceApiException {
        JsonArray arr = new BinanceRequest(baseUrl + "v1/peers")
                .read().asJsonArray();
        List<BinancePeer> peers = new ArrayList<>();
        for (JsonElement p : arr) {
            peers.add(new BinancePeer(p.getAsJsonObject()));
        }
        return peers;
    }

//    /**
//     * Gets the list of validators used in consensus.
//     * @return result in JSON
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonArray getValidators() throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v1/validators"))
//                .read().asJsonArray();
//    }

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
    public BinanceDepth getDepth(BinanceSymbol symbol) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v1/depth?symbol=" + symbol.getSymbol())
                .read().asJsonObject();
        JsonArray asks = ob.get("asks").getAsJsonArray();
        JsonArray bids = ob.get("bids").getAsJsonArray();
        return new BinanceDepth(symbol.getSymbol(),
                // Price and qty in decimal form, e.g. 1.00000000
                new BinanceBidOrAsk(BinanceBidType.ASK, asks),
                new BinanceBidOrAsk(BinanceBidType.BID, bids));
    }

    /**
     * Get latest bids and ask prices, with limit explicitly set.
     *
     * @param symbol Symbol pair, i.e. BNBBTC
     * @param limit numeric limit of results
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    public BinanceDepth getDepth(BinanceSymbol symbol, int limit) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v1/depth?symbol=" + symbol.getSymbol() + "&limit=" + limit)
                .read().asJsonObject();
        JsonArray asks = ob.get("asks").getAsJsonArray();
        JsonArray bids = ob.get("bids").getAsJsonArray();
        return new BinanceDepth(symbol.getSymbol(),
                // Price and qty in decimal form, e.g. 1.00000000
                new BinanceBidOrAsk(BinanceBidType.ASK, asks),
                new BinanceBidOrAsk(BinanceBidType.BID, bids));
    }

    /**
     * Get the current available trading pairs.
     *
     * @param limit numeric limit of results, default 500; max 1000.
     * @param offset start with 0; default 0.
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    public List<BinancePair> getPairs(int limit, int offset) throws BinanceApiException {
        JsonArray arr = new BinanceRequest(baseUrl + "v1/markets?limit=" + limit + "&offset=" + limit)
                .read().asJsonArray();
        List<BinancePair> pairs = new ArrayList<>();
        arr.forEach(p -> {
            pairs.add(new BinancePair(p.getAsJsonObject()));
        });
        return pairs;
    }

    /**
     * Get the current available trading pairs.
     *
     * @param limit numeric limit of results, default 500; max 1000.
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    public List<BinancePair> getPairs(int limit) throws BinanceApiException {
        return getPairs(limit, 0);
    }

    /**
     * Get the current available trading pairs, with a limit of 500 and offset 0.
     *
     * @return result in JSON
     * @throws BinanceApiException in case of any error
     */
    public List<BinancePair> getPairs() throws BinanceApiException {
        return getPairs(500, 0);
    }

    /**
     * Get compressed, historical trades.
     *
     * @param request the request, not null.
     * @return list of historical trades
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceHistoricalTrade> getHistoricalTrades(BinanceHistoricalTradesRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/historicalTrades" + request.toQueryString();
        String lastResponse = new BinanceRequest(u).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceHistoricalTrade>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
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
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceAggregatedTrades> getAggregatedTrades(BinanceAggregatedTradesRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/aggTrades" + request.toQueryString();
        String lastResponse = new BinanceRequest(u).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceAggregatedTrades>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Kline/candlestick bars for a symbol. Klines are uniquely identified by their open time.
     * if startTime and endTime are not sent, the most recent klines are returned.
     * @param request the request, not null.
     * @return list of candlesticks
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceCandlestick> getCandlestickBars(CandlestickBarRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/klines" +request.toQueryString();
        JsonArray jsonElements = new BinanceRequest(u).read().asJsonArray();
        List<BinanceCandlestick> list = new LinkedList<>();
        for (JsonElement e : jsonElements) list.add(new BinanceCandlestick(e.getAsJsonArray()));
        return list;
    }

    /**
     * Exchange info - information about open markets
     * @return BinanceExchangeInfo
     * @throws BinanceApiException in case of any error
     */
    public BinanceExchangeInfo getExchangeInfo() throws BinanceApiException {
        JsonObject jsonObject = (new BinanceRequest(baseUrl + "v3/exchangeInfo"))
                .read().asJsonObject();
        return new BinanceExchangeInfo(jsonObject);
    }

    /**
     * 24hr ticker price change statistics
     * @return json array with prices for all symbols
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTicker24> get24HrPriceStatistics() throws BinanceApiException {
        List<BinanceTicker24> result = new ArrayList<>();
        JsonArray data = new BinanceRequest(baseUrl + "v1/ticker/24hr" )
                .read().asJsonArray();
        data.forEach(d -> {
            BinanceTicker24 ticker = new BinanceTicker24();
            ticker.setSymbol(d.getAsJsonObject().get("symbol").getAsString());
            ticker.read(d.getAsJsonObject());
            result.add(ticker);
        });
        return result;
    }

    /**
     * 24hr ticker price change statistics
     * @param symbol Symbol pair, i.e. BNBBTC
     * @return json with prices
     * @throws BinanceApiException in case of any error
     */
    public BinanceTicker24 get24HrPriceStatistics(BinanceSymbol symbol) throws BinanceApiException {
        BinanceTicker24 ticker = new BinanceTicker24();
        ticker.setSymbol(symbol.getSymbol());
        ticker.read (new BinanceRequest(baseUrl + "v1/ticker/24hr?symbol=" + symbol.toString())
                .read().asJsonObject());
        return ticker;
    }


    /**
     * Get latest price for a symbol.
     *
     * @return last price.
     * @throws BinanceApiException  in case of any error
     */
    public Double getPrice(BinanceSymbol symbol) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v3/ticker/price?symbol="+symbol.getSymbol())
                .read().asJsonObject();
        return ob.get("price").getAsDouble();
    }

    /**
     * Latest price for all symbols -
     *
     * @return Map of big decimals
     * @throws BinanceApiException in case of any error
     */
    public Map<String, Double> getPrices() throws BinanceApiException {
        Map<String, Double> map = new ConcurrentHashMap<>();
        JsonArray array = (new BinanceRequest(baseUrl + "v3/ticker/price"))
                .read().asJsonArray();
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            map.put(obj.get("symbol").getAsString(), obj.get("price").getAsDouble());
        }
        return map;
    }

    /**
     * Get the average price for a symbol.
     * @param symbol the symbol, not null.
     * @return the price found.
     */
    public BinanceAveragePrice getAveragePrice(BinanceSymbol symbol) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v3/avgPrice?symbol="+symbol)
                .read().asJsonObject();
        BinanceAveragePrice price = new BinanceAveragePrice(symbol.getSymbol());
        price.read(ob);
        return price;
    }

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @return map of BinanceTicker
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTicker> getBookTickers() throws BinanceApiException {
        String lastResponse = (new BinanceRequest(baseUrl + "v3/ticker/bookTicker")).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceTicker>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get best price/qty on the order book for all symbols.
     *
     * @return map of BinanceTicker
     * @throws BinanceApiException in case of any error
     */
    public BinanceTicker getBookTicker(BinanceSymbol symbol) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseUrl + "v3/ticker/bookTicker?symbol="+symbol).read().asJsonObject();
        BinanceTicker ticker = new BinanceTicker();
        ticker.read(ob);
        return ticker;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // ACCOUNT READ-ONLY ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Getting account information
     * @return JsonObject
     * @throws BinanceApiException in case of any error
     */
    public BinanceAccount getAccount() throws BinanceApiException {
        BinanceAccount account = new BinanceAccount();
        account.read (new BinanceRequest(baseUrl + "v3/account")
                .sign(apiKey, secretKey, null).read().asJsonObject());
        return account;
    }

//    /**
//     * Getting account sequence information
//     * @param address the address, required.
//     * @return JsonObject
//     * @throws BinanceApiException in case of any error
//     */
//    public JsonObject getAccountSequence(String address) throws BinanceApiException {
//        return (new BinanceRequest(baseUrl + "v3/account/"+address+"/sequence"))
//                .sign(apiKey, secretKey, null).read().asJsonObject();
//    }

    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param symbol the symbol, required.
     * @param timestamp the target timestamp, required.
     * @return the fee, or null.
     * @throws BinanceApiException in case of any error
     */
    public BinanceTradeFee getTradeFee(BinanceSymbol symbol, long timestamp) throws BinanceApiException {
//        JsonArray arr = new BinanceRequest(baseSapiUrl + "v1/asset/tradeFee?symbol="+symbol+"&timestamp="+timestamp)
//                .sign(apiKey, secretKey, null).read().asJsonArray();
        JsonArray arr = new BinanceRequest(baseSapiUrl + "v1/fees?symbol="+symbol+"&timestamp="+timestamp)
                .sign(apiKey, secretKey, null).read().asJsonArray();
        for (JsonElement tr : arr) {
            BinanceTradeFee fee = new BinanceTradeFee();
            fee.setTimestamp(timestamp);
            fee.setMakerCommission(tr.getAsJsonObject().get("makerCommission").getAsDouble());
            fee.setTakerCommission(tr.getAsJsonObject().get("takerCommission").getAsDouble());
            return fee;
        }
        return null;
    }

    /**
     * Get historical trading fees of the address, including fees of trade/canceled order/expired order.
     * Transfer and other transaction fees are not included. Order by block height DESC. Query
     * Window: Default query window is latest 7 days; The maximum start - end query window is
     * 3 months. Rate Limit: 5 requests per IP per second.
     * @param timestamp the target timestamp, required.
     * @return JsonObject
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTradeFee> getTradeFees(long timestamp) throws BinanceApiException {
//        JsonArray arr = new BinanceRequest(baseSapiUrl + "v1/asset/tradeFee?timestamp="+timestamp)
//                .sign(apiKey, secretKey, null).read().asJsonArray();
        JsonArray arr = new BinanceRequest(baseSapiUrl + "v1/fees?timestamp="+timestamp)
                .sign(apiKey, secretKey, null).read().asJsonArray();
        List<BinanceTradeFee> fees = new ArrayList<>();
        for (JsonElement tr : arr) {
            BinanceTradeFee fee = new BinanceTradeFee();
            fee.setTimestamp(timestamp);
            fee.setMakerCommission(tr.getAsJsonObject().get("makerCommission").getAsDouble());
            fee.setTakerCommission(tr.getAsJsonObject().get("takerCommission").getAsDouble());
            fees.add(fee);
        }
        return fees;
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
	 * @throws BinanceApiException in case of any error
	 */
	public List<BinanceOrder> getOpenOrders() throws BinanceApiException {
		String u = baseUrl + "v3/openOrders";
		String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).read().getLastResponse();
		Type listType = new TypeToken<List<BinanceOrder>>() {
		}.getType();
		return new Gson().fromJson(lastResponse, listType);
	}
    
    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceOrder> getOpenOrders(BinanceOpenOrderRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/openOrders" + request.toQueryString();
        String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceOrder>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get all open orders.
     * @param request, required.
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceOrder> cancelOpenOrder(BinanceDeleteOrderRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/openOrders" + request.toQueryString();
        String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).delete().getLastResponse();
        Type listType = new TypeToken<List<BinanceOrder>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get all my orders.
     *
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceOrder> getOrders(BinanceAllOrderRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/allOrders"+request.toQueryString();
        String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceOrder>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get all closed orders.
     *
     * @param request, required
     * @return List of Orders
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceOrder> geClosedOrders(BinanceClosedOrderRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/closedOrders" + request.toQueryString();
        String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceOrder>>() {
        }.getType();
        return new Gson().fromJson(lastResponse, listType);
    }
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
    public List<BinanceOrder> getOrders(BinanceSymbol symbol, Long orderId, int limit) throws BinanceApiException {
        String u = baseUrl + "v3/allOrders?symbol=" + symbol.toString() + "&limit=" + limit;
        if (orderId != null && orderId > 0) u += "&orderId=" + orderId;

        String lastResponse = (new BinanceRequest(u)).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceOrder>>() {}.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * short version of allOrders
     * @param symbol i.e. BNBBTC
     * @return list of orders
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceOrder> getOrders(BinanceSymbol symbol) throws BinanceApiException {
        return getOrders(symbol, 0L, 500);
    }

    /**
     * Get my trades for a specific account and symbol.
     *
     * @param request the request, not null.
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTrade> getMyTrades(BinanceMyTradesRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/myTrades" + request.toQueryString();
        String lastResponse = new BinanceRequest(u).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceTrade>>() {}.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get trades for a specific symbol.
     *
     * @param symbol i.e. BNBBTC
     * @param limit numeric limit of results
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTrade> getTrades(BinanceSymbol symbol, int limit) throws BinanceApiException {
        String u = baseUrl + "v3/trades?symbol=" + symbol + "&limit=" + limit;
        String lastResponse = new BinanceRequest(u).sign(apiKey, secretKey, null).read().getLastResponse();
        Type listType = new TypeToken<List<BinanceTrade>>() {}.getType();
        return new Gson().fromJson(lastResponse, listType);
    }

    /**
     * Get trades for a specific symbol, using default limit=500.
     *
     * @param symbol i.e. BNBBTC
     * @return list of trades
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceTrade> getTrades(BinanceSymbol symbol) throws BinanceApiException {
        return getTrades(symbol, 500);
    }

    /**
     * Get order status and details.
     *
     * @param request the request, required.
     * @return BinanceOrder object if successfull
     * @throws BinanceApiException in case of any error
     */
    public BinanceOrder getOrder(BinanceOrderRequest request) throws BinanceApiException {
        String u = baseUrl + "v3/order"+request.toQueryString();
        String lastResponse = new BinanceRequest(u).sign(apiKey, secretKey, null).read().getLastResponse();
        return (new Gson()).fromJson(lastResponse, BinanceOrder.class);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // TRADING ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    public BinanceNewOrder createOrder(BinanceOrderPlacement orderPlacement)  throws BinanceApiException {
        String u = baseUrl + "v3/order?" + orderPlacement.getAsQuery();
        String lastResponse = new BinanceRequest(u).sign(apiKey, secretKey, null).post().read().getLastResponse();
        return (new Gson()).fromJson(lastResponse, BinanceNewOrder.class);
    }

    /**
     * @param orderPlacement class for order placement
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    public BinanceNewOrder testOrder(BinanceOrderPlacement orderPlacement)  throws BinanceApiException {
        String u = baseUrl + "v3/order/test?" + orderPlacement.getAsQuery();
        String lastResponse = new BinanceRequest(u).sign(apiKey, secretKey, null).post().read().getLastResponse();
        return (new Gson()).fromJson(lastResponse, BinanceNewOrder.class);
    }

    /**
     * Deletes order by order ID
     * @param symbol i.e. "BNBBTC"
     * @param orderId numeric Order ID
     * @return json result from order placement
     * @throws BinanceApiException in case of any error
     */
    public BinanceDeletedOrder deleteOrderById(BinanceSymbol symbol, Long orderId) throws BinanceApiException {
        String u = baseUrl + "v3/order?symbol=" + symbol.toString() + "&orderId=" + orderId;
        JsonObject ob = (new BinanceRequest(u)).sign(apiKey, secretKey, null).delete().read().asJsonObject();
        return new BinanceDeletedOrder(ob);
    }
    /**
     * Deletes order by original client ID
     * @param symbol i.e. "BNBBTC"
     * @param origClientOrderId string order ID, generated by client
     * @return json result
     * @throws BinanceApiException in case of any error
     */
    public BinanceDeletedOrder deleteOrderByOrigClientId(BinanceSymbol symbol, String origClientOrderId) throws BinanceApiException {
        String u = baseUrl + "v3/order?symbol=" + symbol.toString() + "&origClientOrderId=" + esc.escape(origClientOrderId);
        JsonObject ob = (new BinanceRequest(u)).sign(apiKey, secretKey, null).delete().read().asJsonObject();
        return new BinanceDeletedOrder(ob);
    }

    /**
     * Deletes order by new client ID
     * @param symbol i.e. "BNBBTC"
     * @param newClientOrderId string order ID, generated by server
     * @return json result
     * @throws BinanceApiException in case of any error
     */
    public BinanceDeletedOrder deleteOrderByNewClientId(BinanceSymbol symbol, String newClientOrderId ) throws BinanceApiException {
        String u = baseUrl + "v3/order?symbol=" + symbol.toString() + "&newClientOrderId=" + esc.escape(newClientOrderId);
        JsonObject ob = (new BinanceRequest(u)).sign(apiKey, secretKey, null).delete().read().asJsonObject();
        return new BinanceDeletedOrder(ob);
    }

    /**`
     * Deletes order by BinanceOrder object
     * @param order object of existing order
     * @return json result
     * @throws BinanceApiException in case of any error
     */
    public BinanceDeletedOrder deleteOrder(BinanceOrder order) throws BinanceApiException {
        BinanceSymbol symbol = BinanceSymbol.valueOf(order.getSymbol());
        if (!Strings.isNullOrEmpty(order.getClientOrderId())) {
            return deleteOrderByOrigClientId(symbol, order.getClientOrderId());
        }
        return deleteOrderById(symbol, order.getOrderId());
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
    public String startUserDataStream() throws BinanceApiException {
        JsonObject jsonObject = (new BinanceRequest(baseUrl + "v3/userDataStream"))
                .sign(apiKey).post().read().asJsonObject();
        return jsonObject.get("listenKey").getAsString();
    }

    /**
     * Keep user data stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    public void keepUserDataStream(String listenKey) throws BinanceApiException{
        new BinanceRequest(baseUrl + "v3/userDataStream?listenKey=" + esc.escape(listenKey))
                    .sign(apiKey).put().read().asJsonObject();
    }

    /**
     * Close user data stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    public void deleteUserDataStream(String listenKey) throws BinanceApiException {
        new BinanceRequest(baseUrl + "v3/userDataStream?listenKey=" + esc.escape(listenKey))
                .sign(apiKey).delete().read();
    }

    /**
     * Start a new isolated margin stream. The stream will close after 60 minutes unless a keepalive is sent.
     * If the account has an active listenKey, that listenKey will be returned and its validity will be
     * extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    public String startIsolatedMarginStream() throws BinanceApiException {
        JsonObject jsonObject = (new BinanceRequest(baseSapiUrl + "v1/userDataStream/isolated"))
                .sign(apiKey).post().read().asJsonObject();
        return jsonObject.get("listenKey").getAsString();
    }

    /**
     * Keep isolated margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    public void keepIsolatedMarginStream(String listenKey) throws BinanceApiException{
        new BinanceRequest(baseSapiUrl + "v1/userDataStream/isolated?listenKey=" + esc.escape(listenKey))
                .sign(apiKey).put().read().asJsonObject();
    }

    /**
     * Close isolated margin data stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    public void deleteIsolatedMarginStream(String listenKey) throws BinanceApiException {
        new BinanceRequest(baseSapiUrl + "v1/userDataStream/isolated?listenKey=" + esc.escape(listenKey))
                .sign(apiKey).delete().read();
    }

    /**
     * Start a new margin stream. The stream will close after 60 minutes unless a keepalive is sent. If the account has an active listenKey, that listenKey will be returned and its validity will be extended for 60 minutes.
     *
     * Weight: 1.
     * @return listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    public String startMarginStream() throws BinanceApiException {
        JsonObject jsonObject = (new BinanceRequest(baseSapiUrl + "v1/userDataStream"))
                .sign(apiKey).post().read().asJsonObject();
        return jsonObject.get("listenKey").getAsString();
    }

    /**
     * Keep user margin stream alive
     * @param listenKey - key that could be used to manage stream
     * @throws BinanceApiException in case of any error
     */
    public void keepMarginStream(String listenKey) throws BinanceApiException{
        new BinanceRequest(baseSapiUrl + "v1/userDataStream?listenKey=" + esc.escape(listenKey))
                .sign(apiKey).put().read().asJsonObject();
    }

    /**
     * Close margin stream
     * @param listenKey key for user stream management
     * @throws BinanceApiException in case of any error
     */
    public void deleteMarginStream(String listenKey) throws BinanceApiException {
        new BinanceRequest(baseSapiUrl + "v1/userDataStream?listenKey=" + esc.escape(listenKey))
                .sign(apiKey).delete().read();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // FIAT ENDPOINTS
    // - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceFiatOrder> getFiatOrders(BinanceFiatOrderRequest request) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseSapiUrl + "v1/fiat/orders"+request.toQueryString())
                .sign(apiKey, secretKey, null).read().asJsonObject();
        if(ob.has("data")){
            JsonArray arr = ob.get("data").getAsJsonArray();
            Type listType = new TypeToken<List<BinanceFiatOrder>>() {}.getType();
            return new Gson().fromJson(arr, listType);
        }
        return Collections.emptyList();
    }

    /**
     * Get fiat orders.
     *
     * @param request the fiat order request, not null.
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceFiatPayment> getFiatPayments(BinanceFiatOrderRequest request) throws BinanceApiException {
        JsonObject ob = new BinanceRequest(baseSapiUrl + "v1/fiat/payments"+request.toQueryString())
                .sign(apiKey, secretKey, null).read().asJsonObject();
        if(ob.has("data")){
            JsonArray arr = ob.get("data").getAsJsonArray();
            Type listType = new TypeToken<List<BinanceFiatPayment>>() {}.getType();
            return new Gson().fromJson(arr, listType);
        }
        return Collections.emptyList();
    }

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
    public Session getWebsocketSession(String url, WebSocketAdapter adapter) throws BinanceApiException {
        try {
            URI uri = new URI(websocketBaseUrl + url);
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setTrustAll(true); // The magic
            WebSocketClient client = new WebSocketClient(sslContextFactory);
            client.start();
            return client.connect(adapter, uri).get();
        } catch (URISyntaxException e) {
            throw new BinanceApiException("URL Syntax error: " + e.getMessage());
        } catch (Throwable e) {
            throw new BinanceApiException("Websocket error: " + e.getMessage());
        }
    }

    /**
     * Depth Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the event
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketDepth(BinanceSymbol symbol, BinanceWebSocketAdapterDepth adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@depth", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 20 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketDepth20(BinanceSymbol symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@depth20", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 10 levels
     * @param symbol i.e. "BNBBTC"
     * @param adapter  class to handle the events
     * @return  web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketDepth10(BinanceSymbol symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@depth10", adapter);
    }

    /**
     * Depth Websocket Stream Listener - best 5 lavels
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketDepth5(BinanceSymbol symbol, BinanceWebSocketAdapterDepthLevel adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@depth5", adapter);
    }

    /**
     * Klines Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param interval  valid time interval, see BinanceInterval enum
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketKlines(BinanceSymbol symbol, BinanceInterval interval, BinanceWebSocketAdapterKline adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@kline_" + interval.toString(), adapter);
    }

    /**
     * Trades Websocket Stream Listener
     * @param symbol i.e. "BNBBTC"
     * @param adapter class to handle the events
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocketTrades(BinanceSymbol symbol, BinanceWebSocketAdapterAggTrades adapter) throws BinanceApiException {
        return getWebsocketSession(symbol.toString().toLowerCase() + "@aggTrade", adapter);
    }

    /**
     * User Data Websocket Stream Listener
     * @param listenKey string, received in startUserDataStream()
     * @param adapter class to handle the event
     * @return web socket session
     * @throws BinanceApiException in case of any error
     */
    public Session websocket(String listenKey, BinanceWebSocketAdapterUserData adapter) throws BinanceApiException {
        return getWebsocketSession(listenKey, adapter);
    }

    /**
     * Withdrawal APIs.W
     *
     * @param withdrawOrder withdrawOrder
     * @return withdraw id
     * @throws BinanceApiException in case of any error
     */
    public String withdraw(BinanceWithdrawOrder withdrawOrder) throws BinanceApiException {
        String u = baseSapiUrl + "/v1/capital/withdraw/apply" + withdrawOrder.toQueryString();
        return (new BinanceRequest(u))
                .sign(apiKey).post().read().asJsonObject().get("id").getAsString();
    }

    /**
     * Getting history of withdrawals.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceWithdrawTransaction> getWithdrawHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        String q = historyFilter.getAsQuery();
        String u = baseSapiUrl + "/v1/capital/withdraw/history" + (Strings.isNullOrEmpty(q) ? "": ("?" + q));
        List<BinanceWithdrawTransaction> result = new ArrayList<>();
        JsonArray array = new BinanceRequest(u).sign(apiKey).post().read().asJsonArray();
        array.forEach(el -> {
            JsonObject ob = el.getAsJsonObject();
            BinanceWithdrawTransaction tx = new BinanceWithdrawTransaction();
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
    }

    /**
     * Getting history of deposits.
     * So far response is string. at the moment of writing
     * there is a response in Chinese about parameter exception (which cannot be parsed by JSON),
     * and someone seems to still work on that part of server side
     * @param historyFilter structure for user's history filtration
     * @return Temporary returns String until WAPI will be fixed
     * @throws BinanceApiException in case of any error
     */
    public List<BinanceDepositTransaction> getDepositHistory(BinanceHistoryFilter historyFilter) throws BinanceApiException {
        String q = historyFilter.getAsQuery();
        String u = baseSapiUrl + "v1/capital/deposit/hisrec" + (Strings.isNullOrEmpty(q) ? "": ("?" + q));
        List<BinanceDepositTransaction> result = new ArrayList<>();
        JsonArray array = new BinanceRequest(u).sign(apiKey).post().read().asJsonArray();
        array.forEach(el -> {
            JsonObject ob = el.getAsJsonObject();
            BinanceDepositTransaction tx = new BinanceDepositTransaction();
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
    }

    /**
    * Getting status of the system.
    * @return Temporary returns JsonObject
    * @throws BinanceApiException in case of any error
    */
    public BinanceSystemStatus getSystemStatus() throws BinanceApiException {
        String u = baseUrl + "/sapi/v1/system/status";
        JsonObject ob = new BinanceRequest(u).read().asJsonObject();
        BinanceSystemStatus status = new BinanceSystemStatus();
        status.setStatus(ob.get("status").getAsInt());
        status.setMessage(ob.get("msg").getAsString());
        return status;
    }
}


