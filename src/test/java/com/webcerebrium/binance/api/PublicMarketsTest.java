package com.webcerebrium.binance.api;
/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.ExchangeInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

@Slf4j

public class PublicMarketsTest {
    private Api binanceApi = null;

    @Before
    public void setUp() throws Exception {
        binanceApi = new DefaultApi();
    }

    @Test
    public void testPublicMarkets() throws ApiException {
        ExchangeInfo binanceExchangeStats = binanceApi.getExchangeInfo();
        log.info("Public Exchange Stats (not documented): {}", binanceExchangeStats.toString());
    }

    @Test
    public void testExchangeInfo() throws ApiException {
        ExchangeInfo binanceExchangeInfo = binanceApi.getExchangeInfo();
        Set<String> symbols = binanceExchangeInfo.getSymbols();
        // BinanceExchangeSymbol BNB = symbols.stream().filter(a -> a.getQuoteAsset().equals("BNB")).findFirst().get();
        // log.info("BNB Lot Size: {}", BNB.getLotSize().toString());
        symbols
        .stream()
        .filter(b -> (binanceExchangeInfo.getSymbol(b).getBaseAsset().equals("BNB") ||
                binanceExchangeInfo.getSymbol(b).getQuoteAsset().equals("BNB")))
        .forEach(a -> log.info("Base: {} Quote: {} Lot Size: {} Min Notional: {}",
                binanceExchangeInfo.getSymbol(a).getBaseAsset(),
                binanceExchangeInfo.getSymbol(a).getQuoteAsset(),
                binanceExchangeInfo.getSymbol(a).getLotSize().toString(),
                binanceExchangeInfo.getSymbol(a).getMinNotionalValue() ));
    }
}
