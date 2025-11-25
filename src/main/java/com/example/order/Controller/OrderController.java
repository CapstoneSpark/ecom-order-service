package com.example.order.Controller;
import com.example.order.DTO.*;
import com.example.order.Service.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
	@Autowired
    private OrderService orderService;

    public OrderController(OrderService orderService) {
		// TODO Auto-generated constructor stub
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

    // Update order status (admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable("id") Long id,
                                                         @RequestParam("status") String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // Cancel order (user/admin)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // Admin: list all orders
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
