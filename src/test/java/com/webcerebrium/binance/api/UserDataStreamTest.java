package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.webcerebrium.binance.datatype.events.ExecutionReportEvent;
import com.webcerebrium.binance.datatype.events.OutboundAccountInfoEvent;
import com.webcerebrium.binance.websocket.WebSocketUserDataAdapter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class UserDataStreamTest {

    private Api binanceApi = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
    }

    @Test
    public void testUserDataStreamIsCreatedAndClosed() throws Exception, ApiException {
        String listenKey = binanceApi.startUserDataStream();
        log.info("LISTEN KEY=" + listenKey);
        Session session = binanceApi.websocket(listenKey, new WebSocketUserDataAdapter() {
            @Override
            public void onOutboundAccountInfo(OutboundAccountInfoEvent event) throws ApiException {
                log.info(event.toString());
            }
            @Override
            public void onExecutionReport(ExecutionReportEvent event) throws ApiException {
                log.info(event.toString());
            }
        });
        Thread.sleep(2000);
        log.info("KEEPING ALIVE...");
        binanceApi.keepUserDataStream(listenKey);
        Thread.sleep(2000);
        session.close();
        log.info("DELETED...");
        binanceApi.deleteUserDataStream(listenKey);
    }
}

