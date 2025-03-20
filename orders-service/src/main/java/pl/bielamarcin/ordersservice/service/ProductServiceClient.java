package pl.bielamarcin.ordersservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.bielamarcin.ordersservice.dto.ProductDTO;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ProductDTO> getProductById(UUID productId) {
        try {
            return Optional.ofNullable(restTemplate.getForObject("http://products-service:8081/api/products/" + productId, ProductDTO.class));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return Optional.empty();
            }
            throw e;
        }
    }
}
