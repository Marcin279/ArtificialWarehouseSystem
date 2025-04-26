package pl.bielamarcin.productsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.exception.ProductNotFoundException;
import pl.bielamarcin.productsservice.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductRespDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRespDTO> getProductById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductRespDTO> createProduct(@RequestBody ProductReqDTO productReqDTO) {
        ProductRespDTO productRespDTO = productService.addProduct(productReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRespDTO);
    }

    @PostMapping("/all")
    public ResponseEntity<List<ProductRespDTO>> createAllProducts(@RequestBody List<ProductReqDTO> productReqDTOS) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addAllProducts(productReqDTOS));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRespDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductReqDTO product) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
