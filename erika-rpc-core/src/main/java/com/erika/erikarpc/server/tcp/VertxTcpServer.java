package com.erika.erikarpc.server.tcp;

import com.erika.erikarpc.server.HttpServer;
import com.erika.erikarpc.server.HttpServerHandler;
import com.erika.erikarpc.server.VertxHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

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
//        server.connectHandler(netSocket -> {
//            netSocket.handler((buffer -> {
//                byte[] requestData = buffer.getBytes();
//                byte[] responseData = handleRequest(requestData);
//                netSocket.write(Buffer.buffer(responseData));
//            }));
//        });
        server.connectHandler(new TcpServerHandler());

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
