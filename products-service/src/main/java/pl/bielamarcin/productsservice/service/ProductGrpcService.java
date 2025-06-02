package pl.bielamarcin.productsservice.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.exception.ProductNotFoundException;
import pl.bielamarcin.productsservice.grpc.ProductServiceGrpc;
import pl.bielamarcin.productsservice.grpc.ProductServiceProto;
import pl.bielamarcin.productsservice.model.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductGrpcService.class);

    public ProductGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ProductServiceProto.ProductList> responseObserver) {
        var products = productService.getAllProducts().stream()
                .map(this::mapToGrpcProduct)
                .collect(Collectors.toList());

        ProductServiceProto.ProductList response = ProductServiceProto.ProductList.newBuilder()
                .addAllProducts(products)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("GRPC: All products returned");
    }

    @Override
    public void getProductById(ProductServiceProto.ProductId request, StreamObserver<ProductServiceProto.Product> responseObserver) {
        try {
            ProductRespDTO product = productService.getProductById(UUID.fromString(request.getId()));
            responseObserver.onNext(mapToGrpcProduct(product));
            responseObserver.onCompleted();
            logger.info("GRPC: Product with ID {} returned", request.getId());
        } catch (ProductNotFoundException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createProduct(ProductServiceProto.CreateProductRequest request, StreamObserver<ProductServiceProto.Product> responseObserver) {
        var productReqDTO = new ProductReqDTO(
                request.getName(),
                request.getDescription(),
                BigDecimal.valueOf(request.getPrice()),
                request.getQuantity(),
                ProductCategory.valueOf(request.getCategory())
        );

        ProductRespDTO createdProduct = productService.addProduct(productReqDTO);
        responseObserver.onNext(mapToGrpcProduct(createdProduct));
        responseObserver.onCompleted();
        logger.info("GRPC: Product with ID {} created", createdProduct.id());
    }

    @Override
    public void deleteProduct(ProductServiceProto.ProductId request, StreamObserver<Empty> responseObserver) {
        try {
            productService.deleteProduct(UUID.fromString(request.getId()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (ProductNotFoundException e) {
            responseObserver.onError(e);
        }
    }

    private ProductServiceProto.Product mapToGrpcProduct(ProductRespDTO product) {
        return ProductServiceProto.Product.newBuilder()
                .setId(product.id().toString())
                .setName(product.name())
                .setDescription(product.description())
                .setPrice(product.price().doubleValue())
                .setQuantity(product.quantity())
                .setCategory(product.category().name())
                .build();
    }
}