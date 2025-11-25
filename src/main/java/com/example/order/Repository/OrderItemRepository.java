package com.example.order.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.order.Model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
