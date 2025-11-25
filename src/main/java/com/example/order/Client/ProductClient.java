package com.example.order.Client;
import com.example.order.DTO.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Product client is a small synchronous validator.
 * Replace with WebClient/Feign + resilience patterns in production.
 */
@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;
    
    public ProductClient(RestTemplate restTemplate) {
    	this.restTemplate = restTemplate;
    }

    @Value("${product.service.url}")
    private String productServiceUrl;

    public void validateProducts(List<OrderItemDto> items) {
        // TODO: implement bulk validation call to product service.
        // For now, assume all products are valid.
    }
}
