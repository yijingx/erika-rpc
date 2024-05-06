package com.erika.erikarpc.fault.retry;

import com.erika.erikarpc.spi.SpiLoader;

public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
