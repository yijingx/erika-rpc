package com.erika.example.provider;

import com.erika.RpcApplication;
import com.erika.erikarpc.bootstrap.ProviderBootstrap;
import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.model.ServiceMetaInfo;
import com.erika.erikarpc.model.ServiceRegisterInfo;
import com.erika.erikarpc.registry.LocalRegistry;
import com.erika.erikarpc.registry.Registry;
import com.erika.erikarpc.registry.RegistryFactory;
import com.erika.erikarpc.server.tcp.VertxTcpClient;
import com.erika.erikarpc.server.tcp.VertxTcpServer;
import com.erika.example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
