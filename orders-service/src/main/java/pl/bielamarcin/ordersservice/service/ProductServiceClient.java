package pl.bielamarcin.ordersservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.bielamarcin.ordersservice.dto.ProductDTO;

@Service
public class ProductServiceClient {

    private RestTemplate restTemplate;

    public ProductDTO getProductById(Long productId) {
        return restTemplate.getForObject("http://localhost:8081/api/products/" + productId, ProductDTO.class);
    }
}
