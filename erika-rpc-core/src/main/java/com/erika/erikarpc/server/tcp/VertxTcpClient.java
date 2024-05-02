package com.erika.erikarpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {
    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost", netSocketAsyncResult -> {
            if(netSocketAsyncResult.succeeded()){
                System.out.println("Connected to TCP server");
                NetSocket socket = netSocketAsyncResult.result();
                // send data
//                socket.write("Hello, server!");
                for (int i = 0; i < 1000; i++) {
                    socket.write("Hello, server! Hello, server! Hello, server! Hello, server!");
                }
                // received response
                socket.handler(buffer -> {
                    System.out.println("Received response from server: "+buffer.toString());
                });
            } else{
                System.err.println("Failed to connect to TCP server");
            }
        });
    }
    public static void main(String[] args){
        new VertxTcpClient().start();
    }
}
