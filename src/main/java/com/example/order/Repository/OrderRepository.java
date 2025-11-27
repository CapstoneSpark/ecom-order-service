package com.example.order.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order.Model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdempotencyKey(String idempotencyKey);
    

}
