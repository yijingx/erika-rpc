package com.erika.examplespringbootprovider;

import com.erika.erikarpc.springboot.starter.annotation.RpcService;
import com.erika.example.common.model.User;
import com.erika.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("UserName: "+user.getName());
        return user;
    }
}
