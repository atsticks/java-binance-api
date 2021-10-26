package com.webcerebrium.binance.api;
/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.BinanceExchangeInfo;
import com.webcerebrium.binance.datatype.BinanceExchangeStats;
import com.webcerebrium.binance.datatype.BinanceExchangeSymbol;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

@Slf4j

public class PublicMarketsTest {
    private BinanceApi binanceApi = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApi();
    }

    @Test
    public void testPublicMarkets() throws Exception, BinanceApiException {
        BinanceExchangeStats binanceExchangeStats = binanceApi.getPublicExchangeStats();
        log.info("Public Exchange Stats (not documented): {}", binanceExchangeStats.toString());
    }

    @Test
    public void testExchangeInfo() throws Exception, BinanceApiException {
        BinanceExchangeInfo binanceExchangeInfo = binanceApi.getExchangeInfo();
        Set<String> symbols = binanceExchangeInfo.getSymbols();
        // BinanceExchangeSymbol BNB = symbols.stream().filter(a -> a.getQuoteAsset().equals("BNB")).findFirst().get();
        // log.info("BNB Lot Size: {}", BNB.getLotSize().toString());
        symbols
        .stream()
        .filter(b -> (binanceExchangeInfo.getSymbol(b).getBaseAsset().equals("BNB") ||
                binanceExchangeInfo.getSymbol(b).getQuoteAsset().equals("BNB")))
        .forEach(a -> {
             log.info("Base: {} Quote: {} Lot Size: {} Min Notional: {}",
                     binanceExchangeInfo.getSymbol(a).getBaseAsset(),
                     binanceExchangeInfo.getSymbol(a).getQuoteAsset(),
                     binanceExchangeInfo.getSymbol(a).getLotSize().toString(),
                     binanceExchangeInfo.getSymbol(a).getMinNotionalValue() );
        });
    }
}
