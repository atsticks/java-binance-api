package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.BinanceEventDepthLevelUpdate;
import com.webcerebrium.binance.datatype.BinanceEventDepthUpdate;
import com.webcerebrium.binance.datatype.BinanceSymbol;
import com.webcerebrium.binance.websocket.BinanceWebSocketAdapterDepth;
import com.webcerebrium.binance.websocket.BinanceWebSocketAdapterDepthLevel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DepthStreamTest {

    private BinanceApi binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApiDefault();
        symbol = "ETHBTC";
    }

    @Test
    public void testDepthStreamWatcher() throws Exception, BinanceApiException {
        Session session = binanceApi.websocketDepth(symbol, new BinanceWebSocketAdapterDepth() {
            @Override
            public void onMessage(BinanceEventDepthUpdate message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(3000);
        session.close();
    }

    @Test
    public void testDepth5StreamWatcher() throws Exception, BinanceApiException {
        Session session = binanceApi.websocketDepth5(symbol, new BinanceWebSocketAdapterDepthLevel() {
            @Override
            public void onMessage(BinanceEventDepthLevelUpdate message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(3000);
        session.close();
    }
}
