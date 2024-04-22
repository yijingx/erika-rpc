package com.erika.erikarpc.server;

import com.erika.RpcApplication;
import com.erika.erikarpc.model.RpcRequest;
import com.erika.erikarpc.model.RpcResponse;
import com.erika.erikarpc.registry.LocalRegistry;
import com.erika.erikarpc.serializer.JdkSerializer;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.erikarpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

public class  HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        System.out.println("Received request: "+ request.method()+" "+request.uri());

        // 异步处理 HTTP 请求
        request.bodyHandler(body->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try{
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e){
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest==null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try{
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type","application/json");
        try{
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        }catch (Exception e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
