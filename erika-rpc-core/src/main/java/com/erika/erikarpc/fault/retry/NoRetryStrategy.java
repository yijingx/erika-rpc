package com.erika.erikarpc.fault.retry;

import com.erika.erikarpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class NoRetryStrategy implements RetryStrategy{
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception{
        log.info("NoRetryStrategy************");
        return callable.call();
    }
}
