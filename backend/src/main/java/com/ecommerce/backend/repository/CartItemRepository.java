package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Belirli bir kullanıcıya ait sepet ürünlerini getir
    List<CartItem> findByUser(User user);

    // Belirli kullanıcı ve ürün kombinasyonunu getir (ürün sepette var mı kontrolü için)
    Optional<CartItem> findByUserAndProductId(User user, Long productId);
}
