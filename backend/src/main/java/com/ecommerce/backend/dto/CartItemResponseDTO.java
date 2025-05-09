package com.ecommerce.backend.dto;

import com.ecommerce.backend.model.Product;

public class CartItemResponseDTO {
    private Long id;
    private int quantity;
    private double totalPrice;

    private Product product;
    private Long userId;

    public CartItemResponseDTO(Long id, int quantity, double totalPrice, Product product, Long userId) {
        this.id = id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.product = product;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Product getProduct() {
        return product;
    }

    public Long getUserId() {
        return userId;
    }
}
