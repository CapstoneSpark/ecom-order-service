//
//package com.example.order.ServiceImpl;
//
//import org.springframework.dao.CannotAcquireLockException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.order.DTO.*;
//import com.example.order.Enums.OrderStatus;
//import com.example.order.Exception.ResourceNotFoundException;
//import com.example.order.Model.*;
//import com.example.order.Repository.*;
//import com.example.order.Service.OrderService;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final ShippingAddressRepository shippingAddressRepository;
//
//    public OrderServiceImpl(
//            OrderRepository orderRepository,
//            OrderItemRepository orderItemRepository,
//            ShippingAddressRepository shippingAddressRepository
//    ) {
//        this.orderRepository = orderRepository;
//        this.orderItemRepository = orderItemRepository;
//        this.shippingAddressRepository = shippingAddressRepository;
//    }
//
//    // --------------------------------------------------------------------
//    // CREATE ORDER (called AFTER payment service marks as PAID)
//    // Robust: idempotency + retry on transient DB lock errors + handle duplicates
//    // --------------------------------------------------------------------
//    
//    @Override
//    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
//    public OrderResponseDto createOrder(OrderRequestDto request) {
//
//        // 1) Fast-path idempotency
//        if (request.getIdempotencyKey() != null) {
//            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
//            if (existing.isPresent()) {
//                return toResponse(existing.get());
//            }
//        }
//
//        try {
//            // ---------- CREATE ORDER ----------
//            Order order = new Order();
//            order.setUserId(request.getUserId());
//            order.setCartId(request.getCartId());
//            order.setOrderStatus(OrderStatus.PLACED);
//            order.setPaymentStatus("PAID");
//            order.setIdempotencyKey(request.getIdempotencyKey());
//            order.setItems(new ArrayList<>());
//
//            order = orderRepository.save(order);  // <--- ONLY PLACE WHERE DUPLICATE CAN HAPPEN
//
//            // SHIPPING
//            ShippingDto s = request.getShipping();
//            if (s != null) {
//                ShippingAddress sa = new ShippingAddress();
//                sa.setOrder(order);
//                sa.setFullName(s.getFullName());
//                sa.setPhone(s.getPhone());
//                sa.setAddressLine1(s.getAddressLine1());
//                sa.setAddressLine2(s.getAddressLine2());
//                sa.setCity(s.getCity());
//                sa.setState(s.getState());
//                sa.setPostalCode(s.getPostalCode());
//                sa.setCountry(s.getCountry());
//                shippingAddressRepository.save(sa);
//                order.setShippingAddress(sa);
//            }
//
//            // ITEMS
//            BigDecimal total = BigDecimal.ZERO;
//            for (OrderItemDto dto : request.getItems()) {
//                BigDecimal subtotal = dto.getSubtotal() != null
//                        ? dto.getSubtotal()
//                        : dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
//
//                OrderItem item = new OrderItem();
//                item.setOrder(order);
//                item.setProductId(dto.getProductId());
//                item.setQuantity(dto.getQuantity());
//                item.setUnitPrice(dto.getUnitPrice());
//                item.setSubtotal(subtotal);
//                orderItemRepository.save(item);
//
//                order.getItems().add(item);
//                total = total.add(subtotal);
//            }
//
//            order.setTotalAmount(total);
//            order.setOrderStatus(OrderStatus.PROCESSING);
//            orderRepository.save(order);
//
//            return toResponse(order);
//        }
//        catch (DataIntegrityViolationException ex) {
//
//            // ---- This is the KEY FIX ----
//            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
//            if (existing.isPresent()) {
//                return toResponse(existing.get());
//            }
//
//            throw new RuntimeException("Unexpected Constraint Violation: " + ex.getMessage());
//        }
//    }
//
//
//    // --------------------------------------------------------------------
//    // GET USER ORDER HISTORY
//    // --------------------------------------------------------------------
//    @Override
//    public List<OrderSummaryDto> getUserOrderHistory(Long userId) {
//        return orderRepository.findByUserId(userId).stream()
//                .map(o -> new OrderSummaryDto(
//                        o.getOrderId(),
//                        o.getTotalAmount(),
//                        o.getOrderStatus().name(),
//                        o.getPaymentStatus(),
//                        o.getCreatedAt()
//                )).collect(Collectors.toList());
//    }
//
//    // --------------------------------------------------------------------
//    // GET ORDER BY ID
//    // --------------------------------------------------------------------
//    @Override
//    public OrderResponseDto getOrderById(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
//        return toResponse(order);
//    }
//
//    // --------------------------------------------------------------------
//    // UPDATE STATUS (ADMIN)
//    // --------------------------------------------------------------------
//    @Override
//    @Transactional
//    public OrderResponseDto updateOrderStatus(Long orderId, String status) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
//
//        order.setOrderStatus(OrderStatus.valueOf(status));
//        orderRepository.save(order);
//
//        return toResponse(order);
//    }
//
//    // --------------------------------------------------------------------
//    // CANCEL ORDER
//    // --------------------------------------------------------------------
//    @Override
//    @Transactional
//    public OrderResponseDto cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
//
//        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
//                order.getOrderStatus() == OrderStatus.COMPLETED) {
//            throw new RuntimeException("Cannot cancel shipped or completed orders");
//        }
//
//        order.setOrderStatus(OrderStatus.CANCELLED);
//        orderRepository.save(order);
//
//        return toResponse(order);
//    }
//
//    // --------------------------------------------------------------------
//    // ADMIN - ALL ORDERS
//    // --------------------------------------------------------------------
//    @Override
//    public List<OrderResponseDto> getAllOrders() {
//        return orderRepository.findAll()
//                .stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    // --------------------------------------------------------------------
//    // CONVERT ENTITY TO DTO
//    // --------------------------------------------------------------------
//    private OrderResponseDto toResponse(Order order) {
//
//        List<OrderItemDto> items = order.getItems().stream()
//                .map(i -> new OrderItemDto(
//                        i.getProductId(),
//                        i.getQuantity(),
//                        i.getUnitPrice(),
//                        i.getSubtotal()
//                ))
//                .collect(Collectors.toList());
//
//        ShippingDto shipping = null;
//        if (order.getShippingAddress() != null) {
//            ShippingAddress a = order.getShippingAddress();
//            shipping = new ShippingDto(
//                    a.getFullName(),
//                    a.getPhone(),
//                    a.getAddressLine1(),
//                    a.getAddressLine2(),
//                    a.getCity(),
//                    a.getState(),
//                    a.getPostalCode(),
//                    a.getCountry()
//            );
//        }
//
//        return new OrderResponseDto(
//                order.getOrderId(),
//                order.getUserId(),
//                order.getOrderStatus().name(),
//                order.getPaymentStatus(),
//                order.getTotalAmount(),
//                order.getCreatedAt(),
//                order.getUpdatedAt(),
//                shipping,
//                items
//        );
//    }
//}

package com.example.order.ServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    @PersistenceContext
    private EntityManager em;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ShippingAddressRepository shippingAddressRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shippingAddressRepository = shippingAddressRepository;
    }

    // ============================================================
    // CREATE ORDER — safe against duplicates & Hibernate errors
    // ============================================================
    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = DataIntegrityViolationException.class
    )
    public OrderResponseDto createOrder(OrderRequestDto request) {

        // 1) Fast-path idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return toResponse(existing.get());
            }
        }

        try {
            // ----------------- create order ------------------
            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setCartId(request.getCartId());
            order.setOrderStatus(OrderStatus.PLACED);
            order.setPaymentStatus("PAID");
            order.setIdempotencyKey(request.getIdempotencyKey());
            order.setItems(new ArrayList<>());

            // This is the statement that can cause a duplicate constraint
            order = orderRepository.save(order);

            // ----------------- shipping ----------------------
            ShippingDto s = request.getShipping();
            if (s != null) {
                ShippingAddress sa = new ShippingAddress();
                sa.setOrder(order);
                sa.setFullName(s.getFullName());
                sa.setPhone(s.getPhone());
                sa.setAddressLine1(s.getAddressLine1());
                sa.setAddressLine2(s.getAddressLine2());
                sa.setCity(s.getCity());
                sa.setState(s.getState());
                sa.setPostalCode(s.getPostalCode());
                sa.setCountry(s.getCountry());
                shippingAddressRepository.save(sa);
                order.setShippingAddress(sa);
            }

            // ----------------- items -------------------------
            BigDecimal total = BigDecimal.ZERO;
            if (request.getItems() != null) {
                for (OrderItemDto dto : request.getItems()) {
                    BigDecimal subtotal = dto.getSubtotal() != null
                            ? dto.getSubtotal()
                            : (dto.getUnitPrice() == null ? BigDecimal.ZERO : dto.getUnitPrice()
                            .multiply(BigDecimal.valueOf(dto.getQuantity() == null ? 0 : dto.getQuantity())));

                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProductId(dto.getProductId());
                    item.setQuantity(dto.getQuantity());
                    item.setUnitPrice(dto.getUnitPrice());
                    item.setSubtotal(subtotal);

                    orderItemRepository.save(item);
                    order.getItems().add(item);

                    total = total.add(subtotal);
                }
            }

            order.setTotalAmount(total);
            order.setOrderStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            return toResponse(order);

        } catch (DataIntegrityViolationException ex) {
            /*
             * IMPORTANT:
             *  - Clear the persistence context to avoid Hibernate AssertionFailure
             *    which occurs when the session is partially flushed after an exception.
             *  - Re-check idempotency: if the record already exists we simply return it.
             *  - If it's a genuine unexpected constraint failure and we can't find an existing
             *    order to return, convert the error to a safe HTTP 409 (Conflict) with a short
             *    message so raw DB/stacktrace is not leaked to clients.
             */
            em.clear();

            // Try to find existing by idempotency key (again)
            if (request.getIdempotencyKey() != null) {
                Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
                if (existing.isPresent()) {
                    return toResponse(existing.get());
                }
            }

            // If we reach here: we couldn't recover automatically. Return 409 without raw stack.
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order creation conflict: an order with the same idempotency key or unique constraint already exists."
            );
        }
    }

    // ============================================================
    // GET USER ORDER HISTORY
    // ============================================================
    @Override
    public List<OrderSummaryDto> getUserOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(o -> new OrderSummaryDto(
                        o.getOrderId(),
                        o.getTotalAmount(),
                        o.getOrderStatus().name(),
                        o.getPaymentStatus(),
                        o.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // ============================================================
    // GET ORDER BY ID
    // ============================================================
    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found: " + orderId));
        return toResponse(order);
    }

    // ============================================================
    // UPDATE ORDER STATUS
    // ============================================================
    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found: " + orderId));

        order.setOrderStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);

        return toResponse(order);
    }

    // ============================================================
    // CANCEL ORDER
    // ============================================================
    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel shipped or completed orders");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponse(order);
    }

    // ============================================================
    // ADMIN – ALL ORDERS
    // ============================================================
    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // ENTITY → DTO CONVERSION
    // ============================================================
    private OrderResponseDto toResponse(Order order) {

        List<OrderItemDto> items = order.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()
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
    @Override
    public OrderResponseDto getByIdempotencyKey(String key) {
        Optional<Order> opt = orderRepository.findByIdempotencyKey(key);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException("Order not found for idempotency key: " + key);
        }
        return toResponse(opt.get());
    }
    
    
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderRepository.delete(order);
    }


}
