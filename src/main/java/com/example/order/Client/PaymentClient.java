package com.example.order.Client;
import com.example.order.DTO.OrderRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Payment client stub. Replace with actual Payment microservice or gateway integration.
 */
@Component
public class PaymentClient {

    private final RestTemplate restTemplate;
    
    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    /**
     * Synchronous payment processing stub returning a fake transaction id.
     * In production prefer asynchronous processing and listening to PaymentCompleted events.
     */
    public String processPayment(UUID orderId, OrderRequestDto req, double amount) {
        // TODO: call payment microservice or external gateway.
        return "txn-" + orderId + "-" + System.currentTimeMillis();
    }
}
