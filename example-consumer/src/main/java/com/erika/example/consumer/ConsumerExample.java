package com.erika.example.consumer;

import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
    }
}
