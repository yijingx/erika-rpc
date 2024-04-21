package com.erika.example.provider;

import com.erika.erikarpc.registry.LocalRegistry;
import com.erika.erikarpc.server.HttpServer;
import com.erika.erikarpc.server.VertxHttpServer;
import com.erika.example.common.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
