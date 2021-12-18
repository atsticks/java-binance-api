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

import com.google.gson.JsonObject;
import lombok.*;

import java.util.Comparator;
import java.util.Objects;

@Data
@EqualsAndHashCode(of = {"name", "timestamp"})
@RequiredArgsConstructor
public final class BinanceAveragePrice implements Comparable<BinanceAveragePrice> {
    @NonNull
    private String name;
    private Double price;
    private int mins;
    private long timestamp = System.currentTimeMillis();

    public void read(JsonObject ob){
        price = ob.get("price").getAsDouble();
        mins =  ob.get("mins").getAsInt();
    }

    @Override
    public int compareTo(BinanceAveragePrice o) {
        if(o==null){
            return -1;
        }
        if(price == null && o.price==null){
            return 0;
        }else if(price!=null){
            return -1;
        }else if(o.price!=null){
            return 1;
        }
        return price.compareTo(o.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinanceAveragePrice asset = (BinanceAveragePrice) o;
        return name.equals(asset.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}