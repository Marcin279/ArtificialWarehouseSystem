package pl.bielamarcin.productsservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.exception.ProductNotFoundException;
import pl.bielamarcin.productsservice.mapper.ProductMapper;
import pl.bielamarcin.productsservice.model.Product;
import pl.bielamarcin.productsservice.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final MeterRegistry registry;
    private final ProductMapper productMapper;
    private Timer dbTimer;

    public ProductService(ProductRepository productRepository, MeterRegistry registry, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.registry = registry;
        this.productMapper = productMapper;
    }

    @PostConstruct
    public void init() {
        this.dbTimer = this.registry.timer("db.query.time", "operation", "default");
        logger.info("ProductService initialized with metrics enabled");
    }

    @Transactional(readOnly = true)
    public List<ProductRespDTO> getAllProducts() {
        logger.debug("Fetching all products from database");
        Timer.Sample sample = Timer.start(registry);
        try {
            List<Product> products = productRepository.findAll();
            logger.info("Retrieved {} products from database", products.size());
            return products.stream().map(productMapper::toDTO).toList();
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "findAll"));
        }
    }

    @Transactional(readOnly = true)
    public ProductRespDTO getProductById(UUID id) {
        logger.debug("Fetching product with ID: {}", id);
        Timer.Sample sample = Timer.start(registry);
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Product not found with ID: {}", id);
                        return new ProductNotFoundException("Product not found with ID: " + id);
                    });
            logger.debug("Found product: {}", product.getName());
            return productMapper.toDTO(product);
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "findById"));
        }
    }

    public ProductRespDTO addProduct(ProductReqDTO productReqDTO) {
        logger.debug("Adding new product: {}", productReqDTO.name());
        Timer.Sample sample = Timer.start(registry);
        try {
            Product product = productMapper.toEntity(productReqDTO);
            Product savedProduct = productRepository.save(product);
            logger.info("Successfully created product with ID: {}", savedProduct.getId());
            return productMapper.toDTO(savedProduct);
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "save"));
        }
    }

    public List<ProductRespDTO> addAllProducts(List<ProductReqDTO> productReqDTOS) {
        logger.debug("Adding {} products", productReqDTOS.size());
        Timer.Sample sample = Timer.start(registry);
        try {
            List<Product> products = productReqDTOS.stream()
                    .map(productMapper::toEntity)
                    .toList();
            List<Product> savedProducts = productRepository.saveAll(products);
            logger.info("Successfully created {} products", savedProducts.size());
            return savedProducts.stream()
                    .map(productMapper::toDTO)
                    .toList();
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "saveAll"));
        }
    }

    public ProductRespDTO updateProduct(UUID id, ProductReqDTO updatedProductReqDTO) {
        logger.debug("Updating product with ID: {}", id);
        Timer.Sample sample = Timer.start(registry);
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Cannot update - product not found with ID: {}", id);
                        return new ProductNotFoundException("Product not found with ID: " + id);
                    });

            // Update fields
            product.setName(updatedProductReqDTO.name());
            product.setDescription(updatedProductReqDTO.description());
            product.setPrice(updatedProductReqDTO.price());
            product.setQuantity(updatedProductReqDTO.quantity());
            product.setCategory(updatedProductReqDTO.category());

            Product savedProduct = productRepository.save(product);
            logger.info("Successfully updated product with ID: {}", id);
            return productMapper.toDTO(savedProduct);
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "update"));
        }
    }

    public void deleteProduct(UUID id) {
        logger.debug("Deleting product with ID: {}", id);
        Timer.Sample sample = Timer.start(registry);
        try {
            if (!productRepository.existsById(id)) {
                logger.warn("Cannot delete - product not found with ID: {}", id);
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }
            productRepository.deleteById(id);
            logger.info("Successfully deleted product with ID: {}", id);
        } finally {
            sample.stop(registry.timer("db.query.time", "operation", "delete"));
        }
    }
}
