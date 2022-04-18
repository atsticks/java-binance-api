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

// valid
// 1m,3m,5m,15m,30m,
// 1h,2h,4h,6h,8h,12h,
// 1d,3d,1w,1M

import java.util.Locale;

public enum Interval {

    ONE_MIN("1m"),
    THREE_MIN("3m"),
    FIVE_MIN("5m"),
    FIFTEEN_MIN("15m"),
    THIRTY_MIN("30m"),

    ONE_HOUR("1h"),
    TWO_HOURS("2h"),
    FOUR_HOURS("4h"),
    SIX_HOURS("6h"),
    EIGHT_HOURS("8h"),
    TWELVE_HOURS("12h"),

    ONE_DAY("1d"),
    THREE_DAYS("3d"),
    ONE_WEEK("1w"),
    ONE_MONTH("1M")
    ;
    private String value;

    Interval(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    static public Interval lookup(String val) {
        if (val.equals(ONE_MIN.toString())) return ONE_MIN;
        if (val.equals(THREE_MIN.toString())) return THREE_MIN;
        if (val.equals(FIVE_MIN.toString())) return FIVE_MIN;
        if (val.equals(FIFTEEN_MIN.toString())) return FIFTEEN_MIN;
        if (val.equals(THIRTY_MIN.toString())) return THIRTY_MIN;

        if (val.equals(ONE_HOUR.toString())) return ONE_HOUR;
        if (val.equals(TWO_HOURS.toString())) return TWO_HOURS;
        if (val.equals(FOUR_HOURS.toString())) return FOUR_HOURS;
        if (val.equals(SIX_HOURS.toString())) return SIX_HOURS;
        if (val.equals(EIGHT_HOURS.toString())) return EIGHT_HOURS;
        if (val.equals(TWELVE_HOURS.toString())) return TWELVE_HOURS;

        if (val.equals(ONE_DAY.toString())) return ONE_DAY;
        if (val.equals(THREE_DAYS.toString())) return THREE_DAYS;
        if (val.equals(ONE_WEEK.toString())) return ONE_WEEK;
        return ONE_MONTH;
    }

    public static Interval match(String value){
        value = value.toLowerCase(Locale.ROOT);
        for(Interval iv:values()){
            if(iv.name().toLowerCase(Locale.ROOT).equals(value)){
                return iv;
            }
        }
        return null;
    }

}
