package com.ecommerce.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ✅ Payment — Siparişe ait ödeme bilgisini temsil eder.
 * - Ödeme tutarı, yöntemi, tarihi ve başarılı olup olmadığı bilgilerini içerir.
 * - Sipariş ve kullanıcıyla ilişkilidir.
 */
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;              // 💰 Tutar

    private String paymentMethod;       // 💳 Ödeme yöntemi (ör: STRIPE, PAYPAL)

    private boolean success;            // ✅ Başarılı mı?

    private LocalDateTime paymentDate;  // 🕒 Tarih/zaman

    @ManyToOne
@JoinColumn(name = "order_id", nullable = false)
private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // ——— Getter ve Setter’lar ———

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
