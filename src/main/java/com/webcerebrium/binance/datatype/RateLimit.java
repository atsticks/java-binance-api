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

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

// Example of JSON Data
// {"rateLimitType":"REQUESTS","interval":"MINUTE","limit":1200}


@Data
@Slf4j
public class RateLimit {

    String rateLimitType;
    String interval;
    Integer intervalNum;
    Long limit;

    public RateLimit(JsonObject obj) {
        if (obj.has("rateLimitType") && obj.get("rateLimitType").isJsonPrimitive()) {
            rateLimitType = obj.get("rateLimitType").getAsString();
        }
        if (obj.has("interval") && obj.get("interval").isJsonPrimitive()) {
            interval = obj.get("interval").getAsString();
        }
        if (obj.has("intervalNum") && obj.get("intervalNum").isJsonPrimitive()) {
            intervalNum = obj.get("intervalNum").getAsInt();
        }
        if (obj.has("limit") && obj.get("limit").isJsonPrimitive()) {
            limit = obj.get("limit").getAsLong();
        }
    }

}
