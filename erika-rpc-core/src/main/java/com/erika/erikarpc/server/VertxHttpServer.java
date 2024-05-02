package com.erika.erikarpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    public void doStart(int port){
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
//        server.requestHandler(request->{
//            System.out.println("Received request: "+request.method()+" "+ request.uri());
//
//            request.response()
//                    .putHeader("content-type","text-plain")
//                    .end("Hello from Vert.x HTTP server!");
//        });

        // 启动 HTTP 服务器并监听指定端口
        server.requestHandler(new HttpServerHandler());
        server.listen(port, result->{
            if(result.succeeded()){
                System.out.println("Server is now listening on port "+port);
            } else {
                System.err.println("Failed to start server: "+result.cause());
            }
        });
    }
}
