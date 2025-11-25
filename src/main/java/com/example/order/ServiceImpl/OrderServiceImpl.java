package com.example.order.ServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.DTO.*;
import com.example.order.Enums.OrderStatus;
import com.example.order.Exception.ResourceNotFoundException;
import com.example.order.Model.*;
import com.example.order.Repository.*;
import com.example.order.Service.OrderService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final PaymentRepository paymentRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            ShippingAddressRepository shippingAddressRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.paymentRepository = paymentRepository;
    }
    
    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        if (request.getIdempotencyKey() != null) {
            Optional<Order> optional = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (optional.isPresent()) {
                return toResponse(optional.get());
            }
        }

        Order order = new Order();
        order.setUserId(request.getUserId());  // now Long type
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus("PENDING");
        order.setIdempotencyKey(request.getIdempotencyKey());
        order.setItems(new ArrayList<>());

        order = orderRepository.save(order);

        ShippingDto s = request.getShipping();
        ShippingAddress shipping = new ShippingAddress();
        shipping.setOrder(order);
        shipping.setFullName(s.getFullName());
        shipping.setPhone(s.getPhone());
        shipping.setAddressLine1(s.getAddressLine1());
        shipping.setAddressLine2(s.getAddressLine2());
        shipping.setCity(s.getCity());
        shipping.setState(s.getState());
        shipping.setPostalCode(s.getPostalCode());
        shipping.setCountry(s.getCountry());

        shippingAddressRepository.save(shipping);
        order.setShipping(shipping);

        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (OrderItemDto it : request.getItems()) {
                BigDecimal qty = BigDecimal.valueOf(it.getQuantity() == null ? 0 : it.getQuantity());
                BigDecimal unitPrice = it.getUnitPrice() == null ? BigDecimal.ZERO : it.getUnitPrice();
                BigDecimal subtotal = unitPrice.multiply(qty);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                // Adjust productId to Long:
                item.setProductId(it.getProductId());
                item.setQuantity(it.getQuantity());
                item.setUnitPrice(unitPrice);
                item.setSubtotal(subtotal);

                orderItemRepository.save(item);
                order.getItems().add(item);

                total = total.add(subtotal);
            }
        }

        order.setTotalAmount(total);
        orderRepository.save(order);

        // Mock payment as before
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(UUID.randomUUID().toString());  // txn id remains a string
        payment.setAmount(total);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(java.time.Instant.now());

        paymentRepository.save(payment);

        order.setPaymentEntity(payment);
        order.setPaymentStatus("COMPLETED");
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        return toResponse(order);
    }

    @Override
    public List<OrderSummaryDto> getUserOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> new OrderSummaryDto(
                        order.getOrderId(),
                        order.getTotalAmount(),
                        order.getOrderStatus().name(),
                        order.getPaymentStatus(),
                        order.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setOrderStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel shipped or completed orders");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private OrderResponseDto toResponse(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(it -> new OrderItemDto(
                        it.getProductId(),
                        it.getQuantity(),
                        it.getUnitPrice(),
                        it.getSubtotal()
                ))
                .collect(Collectors.toList());

        ShippingDto shipping = null;
        if (order.getShippingAddress() != null) {
            ShippingAddress a = order.getShippingAddress();
            shipping = new ShippingDto(
                    a.getFullName(),
                    a.getPhone(),
                    a.getAddressLine1(),
                    a.getAddressLine2(),
                    a.getCity(),
                    a.getState(),
                    a.getPostalCode(),
                    a.getCountry()
            );
        }

        return new OrderResponseDto(
                order.getOrderId(),
                order.getUserId(),
                order.getOrderStatus().name(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                shipping,
                items
        );
    }

	
}
