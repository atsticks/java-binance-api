package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BinanceTransactionRequest {
    /** Asset address, required. */
    @NonNull
    private String address;
    /** blockHeight. */
    private Long blockHeight;
    /** start time in Milliseconds. */
    private Long startTime;
    /** end time in Milliseconds */
    private Long endTime;
    /** limit, default 500; max 1000.*/
    private Integer limit = 500;
    /** offset, start with 0; default 0.*/
    private Integer offset = 0;
    /** txAsset. */
    private String txAsset;
    /** order side. 1 for buy and 2 for sell. */
    private BinanceTransactionSide side;
    /** order status list. Allowed value: [Ack, IocExpire, IocNoFill, FullyFill, Canceled, Expired, FailedBlocking, FailedMatching]. */
    private BinanceTransactionType type;

    public String toQueryString(){
        String result = "";
        if(address!=null){
            result += "&address="+address;
        }
        if(blockHeight!=null){
            result += "&blockHeight="+blockHeight;
        }
        if(startTime!=null){
            result += "&startTime="+startTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        if(limit!=null){
            result += "&limit="+limit;
        }
        if(offset!=null){
            result += "&offset="+offset;
        }
        if(side!=null){
            result += "&side="+side;
        }
        if(txAsset!=null){
            result += "&txAsset="+txAsset;
        }
        if(type!=null){
            result += "&type="+type;
        }
        if(result.length()>0){
            return result.replaceFirst("\\&", "?");
        }
        return result;
    }
}
