package com.example.order.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.order.Model.ShippingAddress;
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
}
