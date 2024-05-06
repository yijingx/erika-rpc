package com.erika.erikarpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.erika.RpcApplication;
import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.constant.ProtocolConstant;
import com.erika.erikarpc.constant.RpcConstant;
import com.erika.erikarpc.fault.retry.RetryStrategy;
import com.erika.erikarpc.fault.retry.RetryStrategyFactory;
import com.erika.erikarpc.fault.tolerant.TolerantStrategy;
import com.erika.erikarpc.fault.tolerant.TolerantStrategyFactory;
import com.erika.erikarpc.loadbalancer.LoadBalancer;
import com.erika.erikarpc.loadbalancer.LoadBalancerFactory;
import com.erika.erikarpc.model.RpcRequest;
import com.erika.erikarpc.model.RpcResponse;
import com.erika.erikarpc.model.ServiceMetaInfo;
import com.erika.erikarpc.protocol.*;
import com.erika.erikarpc.registry.Registry;
import com.erika.erikarpc.registry.RegistryFactory;
import com.erika.erikarpc.serializer.JdkSerializer;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.erikarpc.serializer.SerializerFactory;
import com.erika.erikarpc.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // get provider's address from service register
        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if(CollUtil.isEmpty(serviceMetaInfoList)){
            throw new RuntimeException("暂无服务地址");
        }

        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams,serviceMetaInfoList);

        RpcResponse rpcResponse;
        try{
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(()->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        }catch (Exception e){
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }
}
