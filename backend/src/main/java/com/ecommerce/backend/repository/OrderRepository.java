package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ OrderRepository — Siparişlere ait veritabanı işlemlerini yöneten repository.
 * - Kullanıcıya göre sipariş bulma
 * - Sipariş ID'si ve kullanıcı doğrulaması
 * - Belirli durumdaki siparişleri listeleme (örn. PENDING)
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1️⃣ Kullanıcının tüm siparişlerini döner
    List<Order> findByUser(User user);

    // 2️⃣ Belirli bir siparişi kullanıcıya göre döner (güvenlik amaçlı)
    Optional<Order> findByIdAndUser(Long id, User user);

    // 3️⃣ Kullanıcının belirli durumdaki siparişlerini döner
    List<Order> findByUserAndStatus(User user, OrderStatus status);
}