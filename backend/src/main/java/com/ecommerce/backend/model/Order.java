package com.ecommerce.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * ✅ Order — Her kullanıcıya ait bir siparişi temsil eder.
 * - Müşteri adı, sipariş kalemleri, toplam tutar ve sipariş durumu içerir.
 * - Kullanıcı ve sipariş kalemleriyle ilişkilidir.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🧍 Müşteri (kullanıcı) adı
    private String customerName;

    // 💰 Toplam fiyat (OrderItem'ların toplamı)
    private double totalPrice;

    // 👤 Siparişi veren kullanıcı
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate özelliği
    private User user;


    // 📦 Sipariş içeriği (birden fazla ürün olabilir)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    // 📌 Sipariş durumu: PENDING, PROCESSING, SHIPPED, vb.
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    // ——— Constructor ———

    public Order() {}

    public Order(String customerName) {
        this.customerName = customerName;
    }

    // ——— Getter & Setter’lar ———

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 🔁 Siparişin toplam fiyatını yeniden hesaplar ve `totalPrice` alanına yazar.
     * - Sipariş kaydedilmeden önce çağrılabilir.
     */
    public void calculateTotal() {
        this.totalPrice = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    /**
     * 🔍 Siparişin toplam tutarını dinamik olarak döner.
     * - `calculateTotal()` gibi yazmaz, sadece okur.
     */
    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
}
