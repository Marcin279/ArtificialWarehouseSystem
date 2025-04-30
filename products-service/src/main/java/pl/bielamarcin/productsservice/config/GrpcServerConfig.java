package pl.bielamarcin.productsservice.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bielamarcin.productsservice.service.ProductGrpcService;

import jakarta.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {
    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server server;

    @Bean
    public Server grpcServer(ProductGrpcService productGrpcService) throws IOException {
        server = ServerBuilder.forPort(grpcPort)
                .addService(productGrpcService)
                .build()
                .start();

        System.out.println("Serwer gRPC uruchomiony na porcie " + grpcPort);

        return server;
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.shutdown();
        }
    }
}