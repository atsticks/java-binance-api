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

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
public class BinanceFiatOrderRequest {
    /** the owner address. */
    @NonNull
    BinanceFiatTransactionType transactionType;
    Long beginTime;
    Long endTime;
    int page=1;
    int rows = 100;
    /** limit, default 500; max 1000.*/
    @NonNull
    long timestamp = System.currentTimeMillis();
    /** offset, start with 0; default 0.*/
    Long recvWindow = 0L;

    public String toQueryString(){
        String result = "?";
        if(transactionType==BinanceFiatTransactionType.deposit)
            result += "0";
        else
            result += "1";
        if(beginTime!=null){
            result += "&beginTime="+beginTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        result += "&timestamp="+timestamp;
        result += "&page="+page;
        result += "&rows="+rows;
        return result;
    }
}
