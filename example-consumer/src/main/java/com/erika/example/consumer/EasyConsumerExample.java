package com.erika.example.consumer;

import com.erika.erikarpc.proxy.ServiceProxyFactory;
import com.erika.example.common.model.User;
import com.erika.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
//        UserService userService = null;
//        User user = new User();
//        user.setName("Erika");
//        User newUser = userService.getUser(user);
//        if(newUser!=null){
//            System.out.println(newUser.getName());
//        } else{
//            System.out.println("user == null");
//        }


//        UserService userService = new UserServiceProxy();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Erika1");
        User newUser = userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        } else{
            System.out.println("user == null");
        }
    }
}
