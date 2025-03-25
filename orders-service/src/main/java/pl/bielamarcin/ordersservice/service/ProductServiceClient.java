package pl.bielamarcin.ordersservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.bielamarcin.ordersservice.dto.ProductDTO;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ProductServiceClient {

    private static final Logger logger = Logger.getLogger(ProductServiceClient.class.getName());
    private final RestTemplate restTemplate;

    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ProductDTO> getProductById(UUID productId) {
        try {
            logger.info("Fetching product with ID: " + productId);
            String url = "http://products-service:8081/api/products/" + productId;
            logger.info("Request URL: " + url);
            ProductDTO product = restTemplate.getForObject(url, ProductDTO.class);
            logger.info("Fetched product: " + product);
            return Optional.ofNullable(product);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                logger.warning("Product not found: " + productId);
                return Optional.empty();
            }
            throw e;
        }
    }
}