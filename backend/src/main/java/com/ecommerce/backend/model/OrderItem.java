package com.ecommerce.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ürün adı (sipariş anında ürün ismi burada sabitlenir)
    private String productName;

    // Siparişte kaç adet alındığı
    private int quantity;

    // Ürünün birim fiyatı (o anki fiyat sabitlenir)
    private double unitPrice;

    // Toplam fiyat = quantity * unitPrice
    private double totalPrice;

    // Bu öğenin ait olduğu sipariş (Order)
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference  // Sonsuz JSON döngüsünü önler (Order -> OrderItem -> Order -> ...)
    private Order order;

    // ————— Constructor —————

    public OrderItem() {}

    public OrderItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice; // otomatik hesaplama
    }

    // ————— Getter & Setter'lar —————

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateTotalPrice(); // her güncellemede toplam yeniden hesaplanır
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        recalculateTotalPrice();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    // Toplam fiyat hesaplaması
    private void recalculateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
}
