package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class BinanceExchangeFeeRequest {
    /** Asset address, required. */
    @NonNull
    private String address;
    /** start time in Milliseconds. */
    private Long start;
    /** end time in Milliseconds */
    private Long end;
    /** limit, default 500; max 1000.*/
    private Integer limit = 500;
    /** offset, start with 0; default 0.*/
    private Integer offset = 0;
    /** total number required, 0 for not required and 1 for required; default not required, return total=-1 in response. */
    private boolean total;

    public String toQueryString(){
        String result = "";
        if(address!=null){
            result += "&address="+address;
        }
        if(start!=null){
            result += "&start="+start;
        }
        if(end!=null){
            result += "&end="+end;
        }
        if(limit!=null){
            result += "&limit="+limit;
        }
        if(offset!=null){
            result += "&offset="+offset;
        }
        if(total){
            result += "&total=1";
        }
        if(result.length()>0){
            return result.replaceFirst("\\&", "?");
        }
        return result;
    }
}
