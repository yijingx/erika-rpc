package com.erika.erikarpc.config;

import com.erika.erikarpc.fault.retry.RetryStrategyKeys;
import com.erika.erikarpc.fault.tolerant.TolerantStrategyKeys;
import com.erika.erikarpc.loadbalancer.LoadBalancerKeys;
import com.erika.erikarpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    private String name = "erika-rpc";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer serverPort = 8080;

    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;

    private RegistryConfig registryConfig = new RegistryConfig();

    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
