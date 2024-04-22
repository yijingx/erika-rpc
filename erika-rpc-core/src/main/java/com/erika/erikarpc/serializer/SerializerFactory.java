package com.erika.erikarpc.serializer;

import com.erika.erikarpc.spi.SpiLoader;

public class SerializerFactory {

//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
//        put(SerializerKeys.JDK, new JdkSerialzer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer();
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};
    static {
    SpiLoader.load(Serializer.class);
}

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
