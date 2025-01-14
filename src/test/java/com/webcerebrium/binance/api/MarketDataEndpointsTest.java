package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class MarketDataEndpointsTest {

    private Api binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
        symbol = "ETHBTC";
    }

    @Test
    public void testDepthEndpoint() throws Exception, ApiException {
        Depth d = binanceApi.getDepth(symbol);

        assertNotNull("depth response should contain non-empty array bids", d.getBids());
        assertNotNull("depth response should contain non-empty array asks", d.getAsks());
    }

    @Test
    public void testAggTradesEndpoint() throws Exception, ApiException {
        List<AggregatedTrades> binanceAggregatedTrades = binanceApi.getAggregatedTrades(
                AggregatedTradesRequest.builder()
                        .symbol(symbol)
                        .limit(5).build());

        assertTrue("Aggregated trades array should be received", binanceAggregatedTrades.size() > 0);
        // check human-looking getters for the first picked trade
        AggregatedTrades trade = binanceAggregatedTrades.get(0);

        assertTrue("First Trade should contain tradeId", trade.getTradeId() > 0);
        assertTrue("First Trade should contain price", trade.getPrice().compareTo(0d) > 0);
        assertTrue("First Trade should contain quantity", trade.getQuantity().compareTo(0d) > 0);
        assertTrue("First Trade should contain firstTradeId", trade.getFirstTradeId() > 0);
        assertTrue("First Trade should contain lastTradeId", trade.getLastTradeId() > 0);
        assertTrue("First Trade should contain timestamp", trade.getTimestamp() > 0);
    }

    @Test
    public void testAggTradesEndpointWithOptions() throws Exception, ApiException {
        // picking interval of last 15 minutes
        Long timeEnd = new Date().getTime();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -15);
        Long timeStart = cal.getTime().getTime();

        List<AggregatedTrades> binanceAggregatedTrades = binanceApi.getAggregatedTrades(AggregatedTradesRequest.builder()
                .symbol(symbol)
                .limit(5)
                .startTime(timeStart)
                .endTime(timeEnd).build());

        assertTrue("Aggregated trades array should be received", binanceAggregatedTrades.size() > 0);
        // check human-looking getters for the first picked trade
        AggregatedTrades trade = binanceAggregatedTrades.get(0);

        assertTrue("First Trade should contain tradeId", trade.getTradeId() > 0);
        assertTrue("First Trade should contain price", trade.getPrice().compareTo(0d) > 0);
        assertTrue("First Trade should contain quantity", trade.getQuantity().compareTo(0d) > 0);
        assertTrue("First Trade should contain firstTradeId", trade.getFirstTradeId() > 0);
        assertTrue("First Trade should contain lastTradeId", trade.getLastTradeId() > 0);
        assertTrue("First Trade should contain timestamp", trade.getTimestamp() > 0);
    }

    @Test
    public void testIntervalsAreConvertedToStrings() throws Exception {
        assertTrue("15min check", Interval.FIFTEEN_MIN.toString().equals("15m"));
        assertTrue("1 hour check", Interval.ONE_HOUR.toString().equals("1h"));
        assertTrue("1 month check", Interval.ONE_MONTH.toString().equals("1M"));
    }

    @Test
    public void testKlinesEndpoint() throws Exception, ApiException {
        // checking intervals
        List<Candlestick> klines = binanceApi.getCandlestickBars(
                CandlesticksRequest.builder()
                        .symbol(symbol)
                        .interval(Interval.FIFTEEN_MIN)
                        .limit(5).build());
        assertTrue("Klines should return non-empty array of candlesticks", klines.size() > 0);

        Candlestick firstCandlestick = klines.get(0);
        log.info(firstCandlestick.toString());
        assertNotNull("Candlestick should contain open", firstCandlestick.getOpen());
        assertNotNull("Candlestick should contain high", firstCandlestick.getHigh());
        assertNotNull("Candlestick should contain low", firstCandlestick.getLow());
        assertNotNull("Candlestick should contain close", firstCandlestick.getClose());
        assertNotNull("Candlestick should contain openTime", firstCandlestick.getOpenTime());
        assertNotNull("Candlestick should contain closeTime", firstCandlestick.getCloseTime());
        assertNotNull("Candlestick should contain numberOfTrades", firstCandlestick.getNumberOfTrades());
        assertNotNull("Candlestick should contain volume", firstCandlestick.getVolume());
        assertNotNull("Candlestick should contain quoteAssetVolume", firstCandlestick.getQuoteAssetVolume());
        assertNotNull("Candlestick should contain takerBuyBaseAssetVolume", firstCandlestick.getTakerBuyBaseAssetVolume());
        assertNotNull("Candlestick should contain takerBuyQuoteAssetVolume", firstCandlestick.getTakerBuyQuoteAssetVolume());
    }

    @Test
    public void testKlinesEndpointWithOptions() throws Exception, ApiException {
        // picking interval of last 3 days
        Long timeEnd = new Date().getTime();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -3);
        Long timeStart = cal.getTime().getTime();

        List<Candlestick> klines = binanceApi.getCandlestickBars(
                CandlesticksRequest.builder()
                        .symbol(symbol)
                        .interval(Interval.FIFTEEN_MIN)
                        .limit(50)
                        .startTime(timeStart)
                        .endTime(timeEnd).build());
        assertTrue("Klines should return non-empty array of candlesticks", klines.size() > 0);

        Candlestick firstCandlestick = klines.get(0);
        log.info(firstCandlestick.toString());
        assertNotNull("Candlestick should contain open", firstCandlestick.getOpen());
        assertNotNull("Candlestick should contain high", firstCandlestick.getHigh());
        assertNotNull("Candlestick should contain low", firstCandlestick.getLow());
        assertNotNull("Candlestick should contain close", firstCandlestick.getClose());
        assertNotNull("Candlestick should contain openTime", firstCandlestick.getOpenTime());
        assertNotNull("Candlestick should contain closeTime", firstCandlestick.getCloseTime());
        assertNotNull("Candlestick should contain numberOfTrades", firstCandlestick.getNumberOfTrades());
        assertNotNull("Candlestick should contain volume", firstCandlestick.getVolume());
        assertNotNull("Candlestick should contain quoteAssetVolume", firstCandlestick.getQuoteAssetVolume());
        assertNotNull("Candlestick should contain takerBuyBaseAssetVolume", firstCandlestick.getTakerBuyBaseAssetVolume());
        assertNotNull("Candlestick should contain takerBuyQuoteAssetVolume", firstCandlestick.getTakerBuyQuoteAssetVolume());
    }


    @Test
    public void testTicker24hrWithoutSymbolEndpoint() throws Exception, ApiException {
        List<Ticker24> json = binanceApi.get24HrPriceStatistics();
        log.info("{}", json.toString());
    }

    @Test
    public void testTicker24hrEndpoint() throws Exception, ApiException {
        Ticker24 jsonObject = binanceApi.get24HrPriceStatistics(symbol);
        assertNotNull(jsonObject);
    }

    @Test
    public void testAllPricesEndpoint() throws Exception, ApiException {
        Double ethbtc = binanceApi.getPrices().get(symbol.toString());
        assertTrue("There should be price for " + symbol.toString(), ethbtc.compareTo(0d) > 0);
    }

    @Test
    public void testAllBookTickersEndpoint() throws Exception, ApiException {
        List<Ticker> mapTickers = binanceApi.getBookTickers();
        assertTrue("There should be some tickers", mapTickers.size() > 0);

        String s = symbol.toString();
        Ticker binanceTicker = mapTickers.get(0);
        assertNotNull(s + " ticker should have bidQty", binanceTicker.getBidPrice());
        assertNotNull(s + " ticker should have askQty", binanceTicker.getAskPrice());
        assertNotNull(s + " ticker should have bidPrice", binanceTicker.getBidPrice());
        assertNotNull(s + " ticker should have askPrice", binanceTicker.getAskPrice());
    }
}

