package com.erika.erikarpc.fault.tolerant;

import com.erika.erikarpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
