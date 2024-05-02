package com.erika.erikarpc.protocol;

import com.erika.erikarpc.constant.ProtocolConstant;
import com.erika.erikarpc.model.RpcRequest;
import com.erika.erikarpc.model.RpcResponse;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.erikarpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException{
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if(magic!= ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("illegal magic message");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        // read message of certain length
        byte[] bodyBytes = buffer.getBytes(17, 17+ header.getBodyLength());
        // decode msg body
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("protocol for this ProtocolMessage doesn't exist");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(messageTypeEnum==null){
            throw new RuntimeException("type for this ProtocolMessage doesn't exist");
        }
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("Support for this message is not available at this time");
        }
    }

}
