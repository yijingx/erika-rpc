package com.erika.erikarpc.fault.retry;

import com.erika.erikarpc.model.RpcResponse;
import org.junit.Test;

public class RetryStrategyTest {
    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void retryTest(){
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(()->{
                System.out.println("Test retry");
//                throw new RuntimeException("Error mock");
                return new RpcResponse();
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("Retry failure");
            e.printStackTrace();
        }
    }
}
