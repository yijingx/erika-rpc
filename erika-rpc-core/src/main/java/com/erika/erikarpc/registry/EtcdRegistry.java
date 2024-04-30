package com.erika.erikarpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.erika.erikarpc.config.RegistryConfig;
import com.erika.erikarpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvClient;

    private static final String ETCD_ROOT_PATH="/rpc/";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        hearBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key =ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if(cacheServiceMetaInfoList!=null){
            log.info("从缓存中获取服务");
            return cacheServiceMetaInfoList;
        }
        String searchPrefix = ETCD_ROOT_PATH+serviceKey+"/";
        try{
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),getOption)
                    .get().getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
            // write to cache
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e){
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for(String key: localRegisterNodeKeySet){
            try{
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e){
                throw new RuntimeException(key+"节点下线失败");
            }
        }
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void hearBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for(String key: localRegisterNodeKeySet){
                    try{
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key,StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e){
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 监听 消费端
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if(newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8), response ->{
                for(WatchEvent event: response.getEvents()){
                    switch (event.getEventType()){
                        case DELETE:
                            registryServiceCache.clearCache();;
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://localhost:2379")
                .build();
        KV kvclient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // put the key-value
        kvclient.put(key,value).get();

        //get the CompletableFuture
        CompletableFuture<GetResponse> getFuture = kvclient.get(key);

        // get the value from the completableFuture
        GetResponse response = getFuture.get();

        kvclient.delete(key).get();
    }
}
