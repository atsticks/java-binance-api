package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class BinanceExchangeFilter {

    String filterType;
    JsonObject data;

    public BinanceExchangeFilter(){}

    public BinanceExchangeFilter(JsonObject ob) {
        filterType = ob.get("filterType").getAsString();
        data = ob;
    }

    public Long getLong(String member){
        return data.get(member).getAsLong();
    }

    public Integer getInteger(String member){
        return data.get(member).getAsInt();
    }

    public Double getDouble(String member){
        return data.get(member).getAsDouble();
    }

    public Boolean getBoolean(String member){
        return data.get(member).getAsBoolean();
    }

    public JsonObject getObject(String member){
        return data.get(member).getAsJsonObject();
    }
}
