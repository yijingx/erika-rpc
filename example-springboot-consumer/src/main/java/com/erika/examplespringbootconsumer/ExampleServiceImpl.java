package com.erika.examplespringbootconsumer;

import com.erika.erikarpc.springboot.starter.annotation.RpcReference;
import com.erika.example.common.model.User;
import com.erika.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("Erika99");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
