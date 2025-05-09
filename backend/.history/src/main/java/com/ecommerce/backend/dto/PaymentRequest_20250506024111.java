package com.ecommerce.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// JSON gönderirken "userId" ve "orderId" kullanıldığından emin olacağız
public class PaymentRequest {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("paymentMethod")
    private String paymentMethod;

    // + getter/setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
