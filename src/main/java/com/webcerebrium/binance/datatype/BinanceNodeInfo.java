package com.webcerebrium.binance.datatype;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BinanceNodeInfo {

    @Data
    public static class ValidatorInfo {
        String address;
        String pub_key;
        Long voting_power;

        public ValidatorInfo(JsonObject validator_info) {
            address = validator_info.get("address").getAsString();
            pub_key = validator_info.get("pub_key").getAsString();
            voting_power = validator_info.get("voting_power").getAsLong();
        }
    }

    @Data
    public static class SyncInfo {
        String latest_block_hash;
        String latest_app_hash;
        Long latest_block_height;
        String latest_block_time;
        boolean catching_up;

        public SyncInfo(JsonObject sync_info) {
            latest_block_hash = sync_info.get("latest_block_hash").getAsString();
            latest_app_hash = sync_info.get("latest_app_hash").getAsString();
            latest_block_time = sync_info.get("latest_block_time").getAsString();
            latest_block_height = sync_info.get("latest_block_height").getAsLong();
            catching_up = sync_info.get("catching_up").getAsBoolean();
        }
    }

    @Data
    public static class ProtocolVersion {
        Long p2p; // P2P
        Long block;
        Long app;

        public ProtocolVersion(JsonObject sync_info) {
            p2p = sync_info.get("P2P").getAsLong();
            block = sync_info.get("block").getAsLong();
            app = sync_info.get("app").getAsLong();
        }
    }

    @Data
    public static class NodeInfo {
        List<ProtocolVersion> protocolVersions = new ArrayList<>(); // Protocol_Version
        String id; // ID
        String listen_addr;
        String network;
        String version;
        String channels;
        String moniker;
        Object other;

        public NodeInfo(JsonObject node_info) {
            id = node_info.get("ID").getAsString();
            listen_addr = node_info.get("listen_addr").getAsString();
            network = node_info.get("network").getAsString();
            version = node_info.get("version").getAsString();
            channels = node_info.get("channels").getAsString();
            moniker = node_info.get("moniker").getAsString();
            other = node_info.get("other");
            JsonArray protocols = node_info.get("Protocol_Version").getAsJsonArray();
            protocols.forEach(p -> {
                this.protocolVersions.add(new ProtocolVersion(p.getAsJsonObject()));
            });
        }
    }

    ValidatorInfo validatorInfo; // validator_info
    SyncInfo syncInfo; // sync_info;
    NodeInfo nodeInfo; // node_info;

}
