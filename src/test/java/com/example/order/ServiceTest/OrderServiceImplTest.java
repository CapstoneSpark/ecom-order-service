package com.example.order.ServiceTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.order.DTO.*;
import com.example.order.Enums.OrderStatus;
import com.example.order.Exception.ResourceNotFoundException;
import com.example.order.Model.*;
import com.example.order.Model.Order;
import com.example.order.Repository.*;
import com.example.order.ServiceImpl.OrderServiceImpl;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

class OrderServiceImplTest {
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ShippingAddressRepository shippingAddressRepository;
    @Mock private PaymentRepository paymentRepository;

    @InjectMocks private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ReturnsExistingOrder_IfIdempotencyKeyExists() {
        String idempotencyKey = "unique-key1";
        Order existing = new Order();
        existing.setOrderId(1L);
        existing.setOrderStatus(OrderStatus.PROCESSING); // Set status to avoid NPE
        existing.setPaymentStatus("PAID");
        existing.setTotalAmount(BigDecimal.TEN);
        existing.setCreatedAt(Instant.now());
        when(orderRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existing));

        OrderRequestDto request = new OrderRequestDto();
        request.setIdempotencyKey(idempotencyKey);

        OrderResponseDto response = orderService.createOrder(request);

        assertEquals(1L, response.getOrderId());
        assertEquals("PROCESSING", response.getOrderStatus());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_CreatesOrderSuccessfully() {
        OrderRequestDto request = new OrderRequestDto();
        request.setUserId(1L);
        request.setIdempotencyKey("key2");
        request.setPaymentMethod("CREDIT_CARD");

        ShippingDto shippingDto = new ShippingDto("John Doe", "1234567890",
            "123 Main St", "Apt 1", "City", "State", "12345", "Country");
        request.setShipping(shippingDto);

        OrderItemDto itemDto = new OrderItemDto(10L, 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        request.setItems(List.of(itemDto));

        Order savedOrder = new Order();
        savedOrder.setOrderId(100L);
        savedOrder.setOrderStatus(OrderStatus.PLACED);
        savedOrder.setPaymentStatus("PENDING");
        savedOrder.setTotalAmount(new BigDecimal("20.00"));
        savedOrder.setCreatedAt(Instant.now());
        when(orderRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(orderItemRepository.save(any())).thenReturn(new OrderItem());
        when(shippingAddressRepository.save(any())).thenReturn(new ShippingAddress());
        when(paymentRepository.save(any())).thenReturn(new Payment());

        OrderResponseDto response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(100L, response.getOrderId());
        assertEquals("PROCESSING", response.getOrderStatus());
        verify(orderRepository, atLeastOnce()).save(any());
    }

    @Test
    void getOrderById_ThrowsWhenNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void updateOrderStatus_UpdatesCorrectly() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderStatus(OrderStatus.PLACED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponseDto dto = orderService.updateOrderStatus(1L, "SHIPPED");

        assertEquals("SHIPPED", dto.getOrderStatus());
    }

    @Test
    void cancelOrder_AllowsCancellationWhenValid() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderStatus(OrderStatus.PLACED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponseDto dto = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED.name(), dto.getOrderStatus());
    }

    @Test
    void cancelOrder_ThrowsIfAlreadyShippedOrCompleted() {
        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.COMPLETED)) {
            Order order = new Order();
            order.setOrderStatus(status);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1L));
        }
    }

    @Test
    void getUserOrderHistory_ReturnsList() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setUserId(10L);
        order.setTotalAmount(BigDecimal.TEN);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus("PAID");
        order.setCreatedAt(Instant.now());

        when(orderRepository.findByUserId(10L)).thenReturn(List.of(order));

        List<OrderSummaryDto> list = orderService.getUserOrderHistory(10L);

        assertFalse(list.isEmpty());
        assertEquals(1L, list.get(0).getOrderId());
        assertEquals("PLACED", list.get(0).getOrderStatus());
    }

    @Test
    void getAllOrders_ReturnsList() {
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setOrderStatus(OrderStatus.PLACED);
        order1.setPaymentStatus("PAID");
        order1.setTotalAmount(BigDecimal.TEN);
        order1.setCreatedAt(Instant.now());

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setOrderStatus(OrderStatus.SHIPPED);
        order2.setPaymentStatus("PAID");
        order2.setTotalAmount(BigDecimal.valueOf(50));
        order2.setCreatedAt(Instant.now());

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderResponseDto> list = orderService.getAllOrders();

        assertEquals(2, list.size());
        assertEquals("PLACED", list.get(0).getOrderStatus());
        assertEquals("SHIPPED", list.get(1).getOrderStatus());
    }
}
