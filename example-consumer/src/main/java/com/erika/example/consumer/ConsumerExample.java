package com.erika.example.consumer;

import com.erika.erikarpc.config.RpcConfig;
import com.erika.erikarpc.proxy.ServiceProxyFactory;
import com.erika.erikarpc.utils.ConfigUtils;
import com.erika.example.common.model.User;
import com.erika.example.common.service.UserService;

public class ConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Erika!");
        User newUser = userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        } else{
            System.out.println("user == null");
        }
    }
}
