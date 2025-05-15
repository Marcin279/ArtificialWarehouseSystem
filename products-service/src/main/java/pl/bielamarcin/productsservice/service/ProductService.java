package pl.bielamarcin.productsservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.exception.ProductNotFoundException;
import pl.bielamarcin.productsservice.mapper.ProductMapper;
import pl.bielamarcin.productsservice.model.Product;
import pl.bielamarcin.productsservice.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private Timer dbTimer;
    private final MeterRegistry registry;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, MeterRegistry registry, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.registry = registry;
        this.productMapper = productMapper;
    }

    @PostConstruct
    public void init() {
        this.dbTimer = this.registry.timer("db.query.time", "operation", "default");
    }

    public List<ProductRespDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }

    public ProductRespDTO getProductById(UUID id) throws ProductNotFoundException {
        Product product = productRepository.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return productMapper.toDTO(product);
    }

    public ProductRespDTO addProduct(ProductReqDTO productReqDTO) {
        Product product = productRepository.save(productMapper.toEntity(productReqDTO));
        this.dbTimer = this.registry.timer("db.query.time", "operation", "saveAll");
        return productMapper.toDTO(product);
    }


    public List<ProductRespDTO> addAllProducts(List<ProductReqDTO> productReqDTOS) {
        List<Product> products = productReqDTOS.stream()
                .map(productMapper::toEntity)
                .toList();
        List<Product> savedProducts = productRepository.saveAll(products);
        return savedProducts.stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductRespDTO updateProduct(UUID id, ProductReqDTO updatedProductReqDTO) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setName(updatedProductReqDTO.name());
        product.setDescription(updatedProductReqDTO.description());
        product.setPrice(updatedProductReqDTO.price());
        product.setQuantity(updatedProductReqDTO.quantity());
        product.setCategory(updatedProductReqDTO.category());
        return productMapper.toDTO(productRepository.save(product));
    }

    public void deleteProduct(UUID id) throws ProductNotFoundException {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
