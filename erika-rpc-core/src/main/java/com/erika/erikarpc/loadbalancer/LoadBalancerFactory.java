package com.erika.erikarpc.loadbalancer;

import com.erika.erikarpc.spi.SpiLoader;

public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    private static  final LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
