package com.example.order.Service;
import java.util.List;
import java.util.UUID;

import com.example.order.DTO.OrderRequestDto;
import com.example.order.DTO.OrderResponseDto;
import com.example.order.DTO.OrderSummaryDto;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto request);
    List<OrderSummaryDto> getUserOrderHistory(Long userId);
    OrderResponseDto getOrderById(Long orderId);
    OrderResponseDto updateOrderStatus(Long orderId, String status);
    OrderResponseDto cancelOrder(Long orderId);
    List<OrderResponseDto> getAllOrders();
 // in com.example.order.Service.OrderService
    OrderResponseDto getByIdempotencyKey(String key);
    public void deleteOrder(Long orderId);

}
