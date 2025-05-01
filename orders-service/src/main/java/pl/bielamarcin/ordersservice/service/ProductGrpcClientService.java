package pl.bielamarcin.ordersservice.service;

import com.google.protobuf.Empty;
import org.springframework.stereotype.Service;
import pl.bielamarcin.ordersservice.dto.ProductDTO;
import pl.bielamarcin.ordersservice.exception.ProductNotFoundException;
import pl.bielamarcin.productsservice.grpc.ProductServiceGrpc;
import pl.bielamarcin.productsservice.grpc.ProductServiceProto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductGrpcClientService {

    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public ProductGrpcClientService(ProductServiceGrpc.ProductServiceBlockingStub productServiceStub) {
        this.productServiceStub = productServiceStub;
    }

    public List<ProductDTO> getAllProducts() {
        ProductServiceProto.ProductList response = productServiceStub.getAllProducts(Empty.getDefaultInstance());
        return response.getProductsList().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(UUID id) throws ProductNotFoundException {
        try {
            ProductServiceProto.ProductId request = ProductServiceProto.ProductId.newBuilder()
                    .setId(id.toString())
                    .build();
            ProductServiceProto.Product response = productServiceStub.getProductById(request);
            return mapToProductDTO(response);
        } catch (Exception e) {
            throw new ProductNotFoundException("Produkt o ID " + id + " nie został znaleziony");
        }
    }

    private ProductDTO mapToProductDTO(ProductServiceProto.Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(UUID.fromString(product.getId()));
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(BigDecimal.valueOf(product.getPrice()));
        productDTO.setQuantity(product.getQuantity());
        return productDTO;
    }
}