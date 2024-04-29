package com.erika.erikarpc.registry;

import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    void init(RegistryConfig registryConfig);
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     *
     * @param serviceKey 服务键名
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
    void destroy();
}
