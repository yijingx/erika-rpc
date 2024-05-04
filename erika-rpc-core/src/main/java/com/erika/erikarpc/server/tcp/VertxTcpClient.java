package com.erika.erikarpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.erika.RpcApplication;
import com.erika.erikarpc.constant.ProtocolConstant;
import com.erika.erikarpc.model.RpcRequest;
import com.erika.erikarpc.model.RpcResponse;
import com.erika.erikarpc.model.ServiceMetaInfo;
import com.erika.erikarpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), netSocketAsyncResult -> {
            if (netSocketAsyncResult.succeeded()) {
                System.out.println("Connected to TCP server");
                NetSocket socket = netSocketAsyncResult.result();
                // send data, build message
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);
                // encode request
                try {
                    Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                    socket.write(encodeBuffer);
                } catch (IOException e) {
                    throw new RuntimeException("Protocol message encode error");
                }
                // receive response
                socket.handler(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseCompletableFuture.complete(rpcResponseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("Protocol message decode error");
                    }
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
        RpcResponse rpcResponse = responseCompletableFuture.get();
        netClient.close();
        return rpcResponse;
    }

    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", netSocketAsyncResult -> {
            if (netSocketAsyncResult.succeeded()) {
                System.out.println("Connected to TCP server");
                NetSocket socket = netSocketAsyncResult.result();
                // send data
//                socket.write("Hello, server!");
                for (int i = 0; i < 1000; i++) {
                    Buffer buffer = Buffer.buffer();
                    String str = "Hello, server! Hello, server! Hello, server! Hello, server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                // received response
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
