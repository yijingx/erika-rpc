package com.erika.erikarpc.config;

import lombok.Data;

@Data
public class RpcConfig {
    private String name = "erika-rpc";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer serverPort = 8080;
}
