package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.events.AggregatedTradeEvent;
import com.webcerebrium.binance.websocket.WebSocketAggTradesAdapter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class AggTradesStreamTest {

    private Api binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
        symbol = "ETHBTC";
    }

    @Test
    public void testTradesStreamWatcher() throws Exception, ApiException {
        Session session = binanceApi.websocketTrades(symbol, new WebSocketAggTradesAdapter() {
            @Override
            public void onMessage(AggregatedTradeEvent message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(5000);
        session.close();
    }
}
