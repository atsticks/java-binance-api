package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.events.DepthLevelUpdateEvent;
import com.webcerebrium.binance.datatype.events.DepthUpdateEvent;
import com.webcerebrium.binance.websocket.WebSocketDepthAdapter;
import com.webcerebrium.binance.websocket.WebSocketDepthLevelAdapter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DepthStreamTest {

    private Api binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
        symbol = "ETHBTC";
    }

    @Test
    public void testDepthStreamWatcher() throws Exception, ApiException {
        Session session = binanceApi.websocketDepth(symbol, new WebSocketDepthAdapter() {
            @Override
            public void onMessage(DepthUpdateEvent message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(3000);
        session.close();
    }

    @Test
    public void testDepth5StreamWatcher() throws Exception, ApiException {
        Session session = binanceApi.websocketDepth5(symbol, new WebSocketDepthLevelAdapter() {
            @Override
            public void onMessage(DepthLevelUpdateEvent message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(3000);
        session.close();
    }
}
