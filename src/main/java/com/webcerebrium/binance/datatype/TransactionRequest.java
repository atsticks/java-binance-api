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

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TransactionRequest {
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
    private TransactionSide side;
    /** order status list. Allowed value: [Ack, IocExpire, IocNoFill, FullyFill, Canceled, Expired, FailedBlocking, FailedMatching]. */
    private TransactionType type;

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
