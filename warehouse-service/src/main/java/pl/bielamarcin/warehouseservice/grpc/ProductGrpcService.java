package pl.bielamarcin.warehouseservice.grpc;

import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bielamarcin.warehouseservice.dto.ProductDTO;
import pl.bielamarcin.warehouseservice.grpc.product.Product;
import pl.bielamarcin.warehouseservice.grpc.product.ProductId;
import pl.bielamarcin.warehouseservice.grpc.product.ProductList;
import pl.bielamarcin.warehouseservice.grpc.product.ProductServiceGrpc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductGrpcService {

    private static final Logger logger = LoggerFactory.getLogger(ProductGrpcService.class);

    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public ProductGrpcService(ProductServiceGrpc.ProductServiceBlockingStub productServiceStub) {
        this.productServiceStub = productServiceStub;
    }

    public List<ProductDTO> getAllProducts() {
        try {
            ProductList response = productServiceStub.getAllProducts(Empty.getDefaultInstance());
            return response.getProductsList().stream()
                    .map(this::mapToProductDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania produktów przez gRPC", e);
            throw new RuntimeException("Nie udało się pobrać produktów", e);
        }
    }

    public ProductDTO getProductById(UUID id) {
        try {
            ProductId request = ProductId.newBuilder()
                    .setId(id.toString())
                    .build();
            Product response = productServiceStub.getProductById(request);
            return mapToProductDTO(response);
        } catch (Exception e) {
            logger.warn("Nie udało się pobrać produktu o ID {}: {}", id, e.getMessage());
            logger.debug("Szczegóły błędu:", e);
            return createMockProduct(id);
        }
    }

    private ProductDTO createMockProduct(UUID id) {
        logger.info("Tworzenie zastępczego produktu dla ID: {}", id);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setName("Produkt tymczasowy");
        productDTO.setDescription("Ten produkt jest tworzony, gdy nie można pobrać danych z serwisu produktowego");
        productDTO.setPrice(BigDecimal.ZERO);
        productDTO.setQuantity(0);
        return productDTO;
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(UUID.fromString(product.getId()));
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(BigDecimal.valueOf(product.getPrice()));
        productDTO.setQuantity(product.getQuantity());
        return productDTO;
    }
}