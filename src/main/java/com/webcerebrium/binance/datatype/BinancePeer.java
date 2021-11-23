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

package com.webcerebrium.binance.datatype;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BinancePeer {

    /** Authenticated identifier
     example: 8c379d4d3b9995c712665dc9a9414dbde5b30483. */
    String id;
    /** Original listen address before PeersService changed it. */
    String original_listen_addr;
    String listen_addr;
    /** Access address (HTTP). */
    String access_addr;
    /** Stream address (WS). */
    String stream_addr;
    /** Chain ID, example: Binance-Chain-Ganges. */
    String network;
    /** version, example: 0.30.1. */
    String version;
    /** Name, example: data-seed-1. */
    String moniker;
    /** Capabilities, one of: node, qs, ap, ws,  example: node,ap. */
    List<String> capabilities = new ArrayList<>();
    /** Is an accelerated path to a validator node. */
    boolean accelerated;

    public BinancePeer(JsonObject ob) {
        id = ob.get("id").getAsString();
        original_listen_addr = ob.get("original_listen_addr").getAsString();
        listen_addr = ob.get("listen_addr").getAsString();
        stream_addr = ob.get("stream_addr").getAsString();
        access_addr = ob.get("access_addr").getAsString();
        network = ob.get("network").getAsString();
        version = ob.get("version").getAsString();
        moniker = ob.get("moniker").getAsString();
        accelerated = ob.get("accelerated").getAsBoolean();
        JsonArray caps = ob.get("capabilities").getAsJsonArray();
        caps.forEach(c -> {
            this.capabilities.add(c.getAsString());
        });
    }
}
