package com.ecommerce.backend.dto;

public class PaymentRequest {
    private Long userId;
    private Long orderId;
    private double amount;
    private String paymentMethod;
    private String paymentIntentId;
    private Boolean success;
    

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public Boolean getSuccess() { // ✅ eksik olan method
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
