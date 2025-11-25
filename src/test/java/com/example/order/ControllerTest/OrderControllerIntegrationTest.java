package com.example.order.ControllerTest;

import com.example.order.Controller.OrderController;
import com.example.order.DTO.*;
import com.example.order.Service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerIntegrationTest {

    private MockMvc mockMvc;
    private OrderService orderServiceMock;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        orderServiceMock = Mockito.mock(OrderService.class);
        OrderController controller = new OrderController(orderServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder_Success() throws Exception {
        // Response DTO stub
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setOrderId(1L);
        responseDto.setUserId(123L);
        responseDto.setOrderStatus("PLACED");

        when(orderServiceMock.createOrder(any(OrderRequestDto.class))).thenReturn(responseDto);

        // Request DTO
        OrderRequestDto request = new OrderRequestDto();
        request.setUserId(123L);
        request.setPaymentMethod("CREDIT_CARD");
        request.setIdempotencyKey("unique-key-postman");

        ShippingDto shipping = new ShippingDto();
        shipping.setFullName("John Doe");
        shipping.setPhone("1234567890");
        shipping.setAddressLine1("123 Main St");
        shipping.setAddressLine2("Apt 4");
        shipping.setCity("City");
        shipping.setState("State");
        shipping.setPostalCode("12345");
        shipping.setCountry("Country");
        request.setShipping(shipping);

        OrderItemDto item = new OrderItemDto();
        item.setProductId(111L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("19.99"));
        item.setSubtotal(new BigDecimal("39.98"));
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(123L))
                .andExpect(jsonPath("$.orderStatus").value("PLACED"));
    }

    @Test
    void getOrderById_Success() throws Exception {
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setOrderId(55L);
        responseDto.setUserId(200L);

        when(orderServiceMock.getOrderById(55L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/orders/55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(55L))
                .andExpect(jsonPath("$.userId").value(200L));
    }

    @Test
    void getUserOrders_Success() throws Exception {
        OrderSummaryDto summary = new OrderSummaryDto(77L, new BigDecimal("34.50"), "PLACED", "PAID", Instant.now());
        when(orderServiceMock.getUserOrderHistory(222L)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/orders").param("userId", "222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(77L))
                .andExpect(jsonPath("$[0].orderStatus").value("PLACED"));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        OrderResponseDto resp = new OrderResponseDto();
        resp.setOrderId(66L);
        resp.setOrderStatus("COMPLETED");

        when(orderServiceMock.updateOrderStatus(eq(66L), eq("COMPLETED"))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/orders/66/status").param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"));
    }

    @Test
    void cancelOrder_Success() throws Exception {
        OrderResponseDto resp = new OrderResponseDto();
        resp.setOrderId(42L);
        resp.setOrderStatus("CANCELLED");

        when(orderServiceMock.cancelOrder(42L)).thenReturn(resp);

        mockMvc.perform(post("/api/v1/orders/42/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    void getAllOrders_Success() throws Exception {
        OrderResponseDto resp1 = new OrderResponseDto(101L, 10L, "PLACED", "PAID", new BigDecimal("88"), Instant.now(), Instant.now(), null, null);
        OrderResponseDto resp2 = new OrderResponseDto(102L, 11L, "COMPLETED", "PAID", new BigDecimal("99"), Instant.now(), Instant.now(), null, null);
        when(orderServiceMock.getAllOrders()).thenReturn(List.of(resp1, resp2));

        mockMvc.perform(get("/api/v1/orders/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(101L))
                .andExpect(jsonPath("$[1].orderId").value(102L));
    }
}
