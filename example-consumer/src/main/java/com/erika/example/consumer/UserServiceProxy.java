package com.erika.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.erika.erikarpc.model.RpcRequest;
import com.erika.erikarpc.model.RpcResponse;
import com.erika.erikarpc.serializer.JdkSerializer;
import com.erika.erikarpc.serializer.Serializer;
import com.erika.example.common.model.User;
import com.erika.example.common.service.UserService;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    public User getUser(User user){
        Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
