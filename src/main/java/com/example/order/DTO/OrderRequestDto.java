package com.example.order.DTO;

import java.util.List;




public class OrderRequestDto {
    private Long userId;
    private Long cartId; // optional
    private String paymentMethod;
    private String idempotencyKey;
    private ShippingDto shipping;
    private List<OrderItemDto> items;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCartId() {
		return cartId;
	}
	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
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
