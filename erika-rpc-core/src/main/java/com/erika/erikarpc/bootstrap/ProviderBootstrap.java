package com.erika.erikarpc.bootstrap;

import com.erika.RpcApplication;
import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.model.ServiceMetaInfo;
import com.erika.erikarpc.model.ServiceRegisterInfo;
import com.erika.erikarpc.registry.LocalRegistry;
import com.erika.erikarpc.registry.Registry;
import com.erika.erikarpc.registry.RegistryFactory;
import com.erika.erikarpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        RpcApplication.init();

        // register service to registry
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for(ServiceRegisterInfo<?> serviceRegisterInfo: serviceRegisterInfoList){
            String serviceName = serviceRegisterInfo.getServiceName();
            // Local registry
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // register center
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
        }

        // start the server
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
