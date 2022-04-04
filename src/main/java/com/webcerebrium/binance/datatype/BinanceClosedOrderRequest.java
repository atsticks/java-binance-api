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

@Data
@Builder
public class BinanceClosedOrderRequest implements HasSymbol{
    /** start time in Milliseconds. */
    Long start;
    /** end time in Milliseconds */
    Long end;
    /** limit, default 500; max 1000.*/
    Integer limit = 500;
    /** offset, start with 0; default 0.*/
    Integer offset = 0;
    /** order side. 1 for buy and 2 for sell. */
    BinanceOrderSide side;
    /** order status list. Allowed value: [Ack, IocExpire, IocNoFill, FullyFill, Canceled, Expired, FailedBlocking, FailedMatching]. */
    BinanceOrderStatus2 status;
    /** The symbol. */
    String symbol;
    /** total number required, 0 for not required and 1 for required; default not required, return total=-1 in response. */
    boolean total;

    public String toQueryString(){
        String result = "";
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
        if(side!=null){
            result += "&side="+side.getNumeric();
        }
        if(status!=null){
            result += "&status="+status.toString();
        }
        if(symbol!=null){
            result += "&symbol="+symbol.toString();
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
