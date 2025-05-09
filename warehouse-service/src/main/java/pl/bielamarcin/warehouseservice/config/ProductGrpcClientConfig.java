package pl.bielamarcin.warehouseservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bielamarcin.warehouseservice.grpc.product.ProductServiceGrpc;

@Configuration
public class ProductGrpcClientConfig {

    @Value("${grpc.product-service.host:product-service}")
    private String productServiceHost;

    @Value("${grpc.product-service.port:9090}")
    private int productServicePort;

    @Bean
    public ManagedChannel productServiceChannel() {
        return ManagedChannelBuilder.forAddress(productServiceHost, productServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceStub(ManagedChannel productServiceChannel) {
        return ProductServiceGrpc.newBlockingStub(productServiceChannel);
    }
}