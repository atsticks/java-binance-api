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
