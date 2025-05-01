package pl.bielamarcin.ordersservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bielamarcin.productsservice.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcProductClientConfig {

    @Bean
    public ManagedChannel channel() {
        return ManagedChannelBuilder.forAddress("products-service", 9090)
                .usePlaintext()
                .build();
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceStub(ManagedChannel channel) {
        return ProductServiceGrpc.newBlockingStub(channel);
    }
}