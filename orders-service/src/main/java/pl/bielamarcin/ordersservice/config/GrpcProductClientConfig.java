package pl.bielamarcin.ordersservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bielamarcin.productsservice.grpc.ProductServiceGrpc;

@Configuration
public class GrpcProductClientConfig {

    @Bean
    public ManagedChannel productServiceChannel(@Value("${grpc.client.products-service.address}") String address) {
        String host = address.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(address.split(":")[2]);

        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub(ManagedChannel productServiceChannel) {
        return ProductServiceGrpc.newBlockingStub(productServiceChannel);
    }
}