

package com.example.order.controller;

import com.example.order.DTO.*;
import com.example.order.Service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    // Constructor injection (no Lombok required)
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create order (checkout)
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto request) {
        OrderResponseDto resp = orderService.createOrder(request);
        return ResponseEntity.ok(resp);
    }

    // Get user order history (query param userId)
    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getUserOrders(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(orderService.getUserOrderHistory(userId));
    }

    // Get order details
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully.");
    }


    // Update order status (admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable("id") Long id,
                                                         @RequestParam("status") String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
 
    @GetMapping("/by-idempotency/{key}")
    public ResponseEntity<OrderResponseDto> getByIdempotency(@PathVariable("key") String key) {
        OrderResponseDto dto = orderService.getByIdempotencyKey(key);
        return ResponseEntity.ok(dto);
    }

}
