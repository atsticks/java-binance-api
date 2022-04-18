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

package com.webcerebrium.binance.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.ApiException;
import com.webcerebrium.binance.datatype.events.ExecutionReportEvent;
import com.webcerebrium.binance.datatype.events.OutboundAccountInfoEvent;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

@Slf4j
public abstract class WebSocketUserDataAdapter extends WebSocketAdapter {
    @Override
    public void onWebSocketConnect(Session sess) {
        log.debug("onWebSocketConnect: {}", sess);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        log.error("onWebSocketError: {}", cause);
    }

    @Override
    public void onWebSocketText(String message) {
        log.debug("onWebSocketText message={}", message);
        JsonObject operation = (new Gson()).fromJson(message, JsonObject.class);

        try {
            String eventType = operation.get("e").getAsString();
            if (eventType.equals("outboundAccountInfo")) {
                onOutboundAccountInfo(new OutboundAccountInfoEvent(operation));
            } else if (eventType.equals("executionReport")) {
                onExecutionReport(new ExecutionReportEvent(operation));
            } else {
                log.error("Error in websocket message - unknown event Type");
            }
        } catch (ApiException e) {
            log.error("Error in websocket message {}", e.getMessage());
        }
    }

    public abstract void onOutboundAccountInfo(OutboundAccountInfoEvent event) throws ApiException;
    public abstract void onExecutionReport(ExecutionReportEvent event) throws ApiException;
}
