package pl.bielamarcin.productsservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.productsservice.dto.ProductDTO;
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

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }

    public ProductDTO getProductById(UUID id) throws ProductNotFoundException{
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return productMapper.toDTO(product);
    }

    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = productRepository.save(productMapper.toEntity(productDTO));
        return productMapper.toDTO(product);
    }
    public List<ProductDTO> addAllProducts(List<ProductDTO> productDTOs) {
        List<Product> products = productDTOs.stream()
                .map(productMapper::toEntity)
                .toList();
        List<Product> savedProducts = productRepository.saveAll(products);
        return savedProducts.stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductDTO updateProduct(UUID id, ProductDTO updatedProductDTO) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setName(updatedProductDTO.name());
        product.setDescription(updatedProductDTO.description());
        product.setPrice(updatedProductDTO.price());
        product.setQuantity(updatedProductDTO.quantity());
        product.setCategory(updatedProductDTO.category());
        return productMapper.toDTO(productRepository.save(product));
    }

    public void deleteProduct(UUID id) throws ProductNotFoundException {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
