package com.example.order.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
    private ShippingDto shipping;
    private List<OrderItemDto> items;

    public OrderResponseDto(Long orderId, Long userId, String orderStatus, String paymentStatus, BigDecimal totalAmount,
                            Instant createdAt, Instant updatedAt, ShippingDto shipping, List<OrderItemDto> items) {
        super();
        this.orderId = orderId;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shipping = shipping;
        this.items = items;
    }

	public OrderResponseDto() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ShippingDto getShipping() {
		return shipping;
	}

	public void setShipping(ShippingDto shipping) {
		this.shipping = shipping;
	}

	public List<OrderItemDto> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDto> items) {
		this.items = items;
	}
    
}
