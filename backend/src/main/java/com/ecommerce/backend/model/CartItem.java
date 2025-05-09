package com.ecommerce.backend.model;

import jakarta.persistence.*;

/**
 * CartItem — Sepet içinde yer alan her bir ürün satırını temsil eder.
 * Her CartItem bir kullanıcıya ve bir ürüne aittir.
 */
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sepetteki ürün bilgisi (bir ürün, birçok sepette olabilir)
    @ManyToOne
    private Product product;

    // Sepetin sahibi olan kullanıcı
    @ManyToOne
    private User user;

    // Üründen kaç adet eklendiği
    private int quantity;

    // Toplam fiyat (quantity * product.price)
    private double totalPrice;

    /** Boş constructor (JPA için gereklidir) */
    public CartItem() {}

    /**
     * Yeni CartItem oluşturucu
     * @param user   Sepet sahibi
     * @param product Ürün
     * @param quantity Adet
     */
    public CartItem(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }

    // ——————— Getter & Setter ——————————

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    /**
     * Ürün set edildiğinde toplam fiyat da otomatik güncellenir.
     */
    public void setProduct(Product product) {
        this.product = product;
        updateTotalPrice(); // ürün fiyatı değişmiş olabilir
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Miktar güncellendiğinde toplam fiyat yeniden hesaplanır.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice; // elle override edilecekse kullanılabilir
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Toplam fiyatı yeniden hesaplar.
     */
    private void updateTotalPrice() {
        if (this.product != null) {
            this.totalPrice = this.product.getPrice() * this.quantity;
        }
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : null) +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", user=" + (user != null ? user.getFullName() : null) +
                '}';
    }
}
