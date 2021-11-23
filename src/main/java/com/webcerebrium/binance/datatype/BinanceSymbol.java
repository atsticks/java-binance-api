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
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BinanceSymbol {

    private String symbol = "";

    public BinanceSymbol(String symbol)  throws BinanceApiException {
        // sanitizing symbol, preventing from common user-input errors
        if (Strings.isNullOrEmpty(symbol)) {
            throw new BinanceApiException("Symbol cannot be empty. Example: BQXBTC");
        }
        if (symbol.contains(" ")) {
            throw new BinanceApiException("Symbol cannot contain spaces. Example: BQXBTC");
        }
//        if (!symbol.endsWith("BTC") && !symbol.endsWith("ETH")&& !symbol.endsWith("BNB") && !symbol.endsWith("USDT")) {
//            throw new BinanceApiException("Market Symbol should be ending with BTC, ETH, BNB or USDT. Example: BQXBTC. Provided: " + symbol);
//        }
        this.symbol = symbol.replace("_", "").replace("-", "").toUpperCase();
    }

    public String toString() { return this.getSymbol(); }

    public static BinanceSymbol valueOf(String s) throws BinanceApiException {
        return new BinanceSymbol(s);
    }

    public static BinanceSymbol BTC(String pair) throws BinanceApiException {
       return BinanceSymbol.valueOf(pair.toUpperCase() + "BTC");
    }

    public static BinanceSymbol ETH(String pair) throws BinanceApiException {
       return BinanceSymbol.valueOf(pair.toUpperCase() + "ETH");
    }

    public static BinanceSymbol BNB(String pair) throws BinanceApiException {
        return BinanceSymbol.valueOf(pair.toUpperCase() + "BNB");
    }
    public static BinanceSymbol USDT(String pair) throws BinanceApiException {
       return BinanceSymbol.valueOf(pair.toUpperCase() + "USDT");
    }

    public boolean contains(String coin) {
        return (symbol.endsWith(coin.toUpperCase())) || (symbol.startsWith(coin.toUpperCase()));
    }

    public String getOpposite(String coin) {
        if (symbol.startsWith(coin)) {
            return symbol.substring((coin).length());
        }
        if (symbol.endsWith(coin)) {
            int index = symbol.length() - (coin).length();
            return symbol.substring(0, index);
        }
        return "";
    }

}
