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


import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
@Builder
public class BinanceHistoryFilter {
    @NonNull
    private String coin;
    private String withdrawOrderId;
    private Long startTime = null;
    private Long endTime = null;
    private Integer offset = 0;
    private Integer limit = 500;
    private Integer recvWindow = 500;

    public String getAsQuery() {
        StringBuffer sb = new StringBuffer();
        Escaper esc = UrlEscapers.urlFormParameterEscaper();

        sb.append("?coin=").append(esc.escape(coin));
        if (!Strings.isNullOrEmpty(withdrawOrderId)) {
            sb.append("&withdrawOrderId=").append(esc.escape(withdrawOrderId));
        }
        if (startTime != null) {
            sb.append("&startTime=").append(startTime);
        }
        if (endTime != null) {
            sb.append("&endTime=").append(endTime);
        }
        if (offset!=null) {
            sb.append("&offset=").append(offset);
        }
        if (limit!=null) {
            sb.append("&limit=").append(limit);
        }
        if (recvWindow!=null) {
            sb.append("&recvWindow=").append(recvWindow);
        }
        return sb.toString();
    }
}
