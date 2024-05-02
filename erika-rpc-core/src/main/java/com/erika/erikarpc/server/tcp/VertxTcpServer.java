package com.erika.erikarpc.server.tcp;

import com.erika.erikarpc.server.HttpServer;
import com.erika.erikarpc.server.HttpServerHandler;
import com.erika.erikarpc.server.VertxHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

import javax.swing.plaf.IconUIResource;

public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData){
        return "Hello, client".getBytes();
    }
    @Override
    public void doStart(int port){
        Vertx vertx = Vertx.vertx();
        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(netSocket -> {
            netSocket.handler((buffer -> {
//                byte[] requestData = buffer.getBytes();
//                byte[] responseData = handleRequest(requestData);
//                netSocket.write(Buffer.buffer(responseData));
                String testMessage = "Hello, server! Hello, server! Hello, server! Hello, server!";
                int messageLength = testMessage.getBytes().length;
                if(buffer.getBytes().length<messageLength){
                    System.out.println("半包, length = "+buffer.getBytes().length);
                    return;
                }
                if(buffer.getBytes().length>messageLength){
                    System.out.println("粘包, length = "+buffer.getBytes().length);
                    return;
                }
                String str = new String(buffer.getBytes(0, messageLength));
                System.out.println(str);
                if(testMessage.equals(str)){
                    System.out.println("good");
                }
            }));
        });

//        server.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端口
        server.listen(port, netServerAsyncResult -> {
            if(netServerAsyncResult.succeeded()){
                System.out.println("TCP server starts on port: "+port);
            } else{
                System.out.println("Failed to start TCP server: "+netServerAsyncResult.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
