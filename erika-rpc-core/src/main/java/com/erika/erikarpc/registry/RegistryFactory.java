package com.erika.erikarpc.registry;

import com.erika.erikarpc.serializer.JdkSerializer;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.erikarpc.spi.SpiLoader;

public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class, key);
    }
}
