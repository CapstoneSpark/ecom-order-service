package com.example.order.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.order.Model.Payment;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
