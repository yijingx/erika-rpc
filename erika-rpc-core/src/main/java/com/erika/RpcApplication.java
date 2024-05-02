package com.erika;

import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.constant.RpcConstant;
import com.erika.erikarpc.registry.Registry;
import com.erika.erikarpc.registry.RegistryFactory;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.erikarpc.serializer.SerializerFactory;
import com.erika.erikarpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
        // create and register Shutdown Hook, execute when JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
