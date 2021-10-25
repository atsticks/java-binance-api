package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceSystemStatus {
    /** 0: normal, 1: maintenance. */
    private int status;
    /** "normal", "system_maintenance" */
    private String message;
}
