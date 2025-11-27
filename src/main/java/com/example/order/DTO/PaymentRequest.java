package com.example.order.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PaymentRequest {
    @NotNull
    private Long amount; // amount in paise
    private String currency = "INR";
    private Long userId;
    private List<OrderItemDto> items;
    private ShippingDto shipping;

    // getters / setters

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
    public ShippingDto getShipping() { return shipping; }
    public void setShipping(ShippingDto shipping) { this.shipping = shipping; }
}
