package pl.bielamarcin.productsservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.mapper.ProductMapper;
import pl.bielamarcin.productsservice.repository.ProductRepository;
import pl.bielamarcin.productsservice.model.Product;
import pl.bielamarcin.productsservice.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductRespDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }

    public ProductRespDTO getProductById(UUID id) throws ProductNotFoundException{
        Product product = productRepository.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return productMapper.toDTO(product);
    }

    public ProductRespDTO addProduct(ProductReqDTO productReqDTO) {
        Product product = productRepository.save(productMapper.toEntity(productReqDTO));
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
