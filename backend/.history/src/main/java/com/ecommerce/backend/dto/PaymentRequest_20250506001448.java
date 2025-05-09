package com.ecommerce.backend.dto;

/**
 * ✅ PaymentRequest — Kullanıcıdan gelen ödeme bilgilerini temsil eder.
 * - Sipariş ID’si, ödeme yöntemi, ödeme tutarı gibi temel bilgiler içerir.
 * - Ayrıca test ortamı için success (başarı) durumu ve ödeme sağlayıcısı alanları eklendi.
 */
public class PaymentRequest {

    private Long userId;            // 🧍 Kullanıcının ID'si
    private Long orderId;           // 📦 Ödeme yapılacak siparişin ID'si
    private double amount;          // 💰 Ödeme tutarı
    private String paymentMethod;   // 💳 Ödeme yöntemi (CREDIT_CARD, PAYPAL, STRIPE, vb.)
    private Boolean success;        // ✅ Test amaçlı manuel başarı durumu (opsiyonel)
    private String paymentGateway;  // 💼 "stripe" veya "paypal" gibi sağlayıcı (test kontrolü için)

    // ——— Boş Constructor ———
    public PaymentRequest() {}

    // ——— Parametreli Constructor ———
    public PaymentRequest(Long userId, Long orderId, double amount, String paymentMethod,
                          Boolean success, String paymentGateway) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.success = success;
        this.paymentGateway = paymentGateway;
    }

    // ——— Getter & Setter’lar ———
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
}
