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

package com.webcerebrium.binance.datatype.events;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.ApiException;
import com.webcerebrium.binance.datatype.HasSymbol;
import com.webcerebrium.binance.datatype.Interval;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/*
    {
        "e": "kline",							// event type
        "E": 1499404907056,						// event time
        "s": "ETHBTC",							// symbol
        "k": {
            "t": 1499404860000, 				// start time of this bar
            "T": 1499404919999, 				// end time of this bar
            "s": "ETHBTC",						// symbol
            "i": "1m",							// interval
            "f": 77462,							// first trade id
            "L": 77465,							// last trade id
            "o": "0.10278577",					// open
            "c": "0.10278645",					// close
            "h": "0.10278712",					// high
            "l": "0.10278518",					// low
            "v": "17.47929838",					// volume
            "n": 4,								// number of trades
            "x": false,							// whether this bar is final
            "q": "1.79662878",					// quote volume
            "V": "2.34879839",					// volume of active buy
            "Q": "0.24142166",					// quote volume of active buy
            "B": "13279784.01349473"			// can be ignored
            }
        }
    }
*/

@Data
@Slf4j
@EqualsAndHashCode(of = {"symbol", "eventTime", "interval", "startTime"})
public class CandlestickEvent implements HasSymbol {
    public Long eventTime;
    public String symbol;
    public Interval interval;

    public Long startTime;
    public Long endTime;

    public Long firstTradeId;
    public Long lastTradeId;

    public Double open;
    public Double close;
    public Double high;
    public Double low;
    public Double volume;

    public Long numberOfTrades;
    public boolean isFinal;

    public Double quoteVolume;
    public Double volumeOfActiveBuy;
    public Double quoteVolumeOfActiveBuy;

    public CandlestickEvent(JsonObject event) throws ApiException {
        eventTime = event.get("E").getAsLong();
        symbol = event.get("s").getAsString();

        JsonObject k = event.get("k").getAsJsonObject();
        log.debug(k.get("i").getAsString());
        interval = Interval.lookup(k.get("i").getAsString());

        startTime = k.get("t").getAsLong();
        endTime  = k.get("T").getAsLong();

        firstTradeId  = k.get("f").getAsLong();
        lastTradeId  = k.get("L").getAsLong();

        open = k.get("o").getAsDouble();
        close = k.get("c").getAsDouble();
        high = k.get("h").getAsDouble();
        low = k.get("l").getAsDouble();
        volume = k.get("v").getAsDouble();

        numberOfTrades  = k.get("n").getAsLong();
        isFinal  = k.get("x").getAsBoolean();

        quoteVolume = k.get("q").getAsDouble();
        volumeOfActiveBuy = k.get("V").getAsDouble();
        quoteVolumeOfActiveBuy = k.get("Q").getAsDouble();
    }
}
