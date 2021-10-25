//package com.webcerebrium.binance.datatype;
//
//import com.google.gson.JsonObject;
//import lombok.Data;
//
//
//
//
//// Structure from account balance
//
//@Data
//public class BinanceWalletAsset {
//    public String asset;
//    public Double free;
//    public Double locked;
//
//    public BinanceWalletAsset() {
//    }
//
//    public BinanceWalletAsset(JsonObject obj) {
//        if (obj.has("a")) {
//            this.asset = obj.get("a").getAsString();
//        } else {
//            this.asset = obj.get("asset").getAsString();
//        }
//        if (obj.has("f")) {
//            this.free = obj.get("f").getAsDouble();
//        } else {
//            this.free = obj.get("free").getAsDouble();
//        }
//        if (obj.has("l")) {
//            this.locked = obj.get("l").getAsDouble();
//        } else {
//            this.locked = obj.get("locked").getAsDouble();
//        }
//    }
//}
