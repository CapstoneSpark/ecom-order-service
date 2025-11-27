//package com.example.order.Model;
//
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.Instant;
//
//@Entity
//@Table(name = "payments")
//public class Payment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "payment_id")
//    private Long paymentId;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    private Order order;
//
//    @Column(name = "payment_method")
//    private String paymentMethod;
//
//    @Column(name = "transaction_id")
//    private String transactionId;
//
//    @Column(name = "amount")
//    private BigDecimal amount;
//
//    @Column(name = "payment_status")
//    private String paymentStatus;
//
//    @Column(name = "payment_date")
//    private Instant paymentDate;
//
//	public Long getPaymentId() {
//		return paymentId;
//	}
//
//	public void setPaymentId(Long paymentId) {
//		this.paymentId = paymentId;
//	}
//
//	public Order getOrder() {
//		return order;
//	}
//
//	public void setOrder(Order order) {
//		this.order = order;
//	}
//
//	public String getPaymentMethod() {
//		return paymentMethod;
//	}
//
//	public void setPaymentMethod(String paymentMethod) {
//		this.paymentMethod = paymentMethod;
//	}
//
//	public String getTransactionId() {
//		return transactionId;
//	}
//
//	public void setTransactionId(String transactionId) {
//		this.transactionId = transactionId;
//	}
//
//	public BigDecimal getAmount() {
//		return amount;
//	}
//
//	public void setAmount(BigDecimal amount) {
//		this.amount = amount;
//	}
//
//	public String getPaymentStatus() {
//		return paymentStatus;
//	}
//
//	public void setPaymentStatus(String paymentStatus) {
//		this.paymentStatus = paymentStatus;
//	}
//
//	public Instant getPaymentDate() {
//		return paymentDate;
//	}
//
//	public void setPaymentDate(Instant paymentDate) {
//		this.paymentDate = paymentDate;
//	}
//
//    // Getters and setters
//    
//}
