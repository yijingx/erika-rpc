package com.erika.example.provider;

import com.erika.RpcApplication;
import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.model.ServiceMetaInfo;
import com.erika.erikarpc.registry.LocalRegistry;
import com.erika.erikarpc.registry.Registry;
import com.erika.erikarpc.registry.RegistryFactory;
import com.erika.erikarpc.server.tcp.VertxTcpClient;
import com.erika.erikarpc.server.tcp.VertxTcpServer;
import com.erika.example.common.service.UserService;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        // register service
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // register service to registry
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
