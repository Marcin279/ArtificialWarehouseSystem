package pl.bielamarcin.productsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.exception.GlobalExceptionHandler;
import pl.bielamarcin.productsservice.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "API for managing products in the warehouse system")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all products",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class)))
    })
    public ResponseEntity<List<ProductRespDTO>> getAllProducts() {
        logger.info("Fetching all products");
        List<ProductRespDTO> products = productService.getAllProducts();
        logger.info("Found {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<ProductRespDTO> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable UUID id) {
        logger.info("Fetching product with ID: {}", id);
        ProductRespDTO product = productService.getProductById(id);
        logger.info("Found product: {}", product.name());
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Add a new product to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<ProductRespDTO> createProduct(
            @Parameter(description = "Product data", required = true) @Valid @RequestBody ProductReqDTO productReqDTO) {
        logger.info("Creating new product: {}", productReqDTO.name());
        ProductRespDTO productRespDTO = productService.addProduct(productReqDTO);
        logger.info("Created product with ID: {}", productRespDTO.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(productRespDTO);
    }

    @PostMapping("/all")
    @Operation(summary = "Create multiple products", description = "Add multiple products to the system in a single request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Products created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductRespDTO>> createAllProducts(
            @Parameter(description = "List of products to create", required = true) @Valid @RequestBody List<ProductReqDTO> productReqDTOS) {
        logger.info("Creating {} products", productReqDTOS.size());
        List<ProductRespDTO> products = productService.addAllProducts(productReqDTOS);
        logger.info("Created {} products", products.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(products);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<ProductRespDTO> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Updated product data", required = true) @Valid @RequestBody ProductReqDTO product) {
        logger.info("Updating product with ID: {}", id);
        ProductRespDTO updatedProduct = productService.updateProduct(id, product);
        logger.info("Updated product: {}", updatedProduct.name());
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Remove a product from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable UUID id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        logger.info("Deleted product with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
