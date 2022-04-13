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
public class WithdrawOrder {
    @NonNull
    private String coin; // required
    @NonNull
    private String address; // required
    @NonNull
    private Double amount; // required
    private Long timestamp = System.currentTimeMillis(); // required
    /** client id for withdraw. */
    private String withdrawOrderId;
    private String network;
    /** Secondary address identifier for coins like XRP,XMR etc.. */
    private String addressTag;
    /** NO 	When making internal transfer, true for returning the fee to the destination
     * account; false for returning the fee back to the departure account. Default false. */
    private boolean transactionFeeFlag;
    /** Description of the address. Space in name should be encoded into %20. */
    private String name;
    private Long recvWindow;

    public String toQueryString() {
        String result = "";
        result += "?coin="+coin;
        result += "&address="+address;
        result += "&amount="+amount;
        if(timestamp!=null){
            result += "&timestamp="+timestamp;
        }
        if(withdrawOrderId!=null){
            result += "&withdrawOrderId="+withdrawOrderId;
        }
        if(network!=null){
            result += "&network="+network;
        }
        if(addressTag!=null){
            result += "&addressTag="+addressTag;
        }
        result += "&transactionFeeFlag="+transactionFeeFlag;
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        if(name!=null){
            result += "&name="+name;
        }
        return result;
    }
}
