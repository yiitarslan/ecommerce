package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ OrderController — Sipariş işlemlerini yöneten REST Controller.
 * - Sipariş oluşturma
 * - Sipariş listeleme (hepsi / sadece pending olanlar)
 * - Sipariş görüntüleme
 * - Sipariş durumu güncelleme
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    /**
     * 1️⃣ Sepetten sipariş oluşturur.
     * Endpoint: POST /orders/create/{userId}
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Order order = orderService.createOrderFromCart(user);
        return ResponseEntity.ok(order);
    }

    /**
     * 2️⃣ Kullanıcının tüm siparişlerini getirir.
     * Endpoint: GET /orders/all/{userId}
     */
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<Order> orders = orderService.getAllOrders(user);
        return ResponseEntity.ok(orders);
    }

    /**
     * 3️⃣ Kullanıcının sadece PENDING siparişlerini getirir.
     * Endpoint: GET /orders/pending/{userId}
     */
    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<Order>> getPendingOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<Order> pendingOrders = orderService.getOrdersByStatus(user, OrderStatus.PENDING);
        return ResponseEntity.ok(pendingOrders);
    }

    /**
     * 4️⃣ Belirli bir siparişi getirir.
     * Endpoint: GET /orders/{orderId}/user/{userId}
     */
    @GetMapping("/{orderId}/user/{userId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId,
                                          @PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Order order = orderService.getOrderById(orderId, user);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    /**
     * 5️⃣ Siparişin durumunu günceller (örn: SHIPPED, DELIVERED)
     * Endpoint: PUT /orders/updateStatus?id=3&status=SHIPPED
     */
    @PutMapping("/updateStatus")
    public ResponseEntity<Order> updateOrderStatus(@RequestParam Long id,
                                                   @RequestParam OrderStatus status) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        order.setStatus(status);
        Order updated = orderService.saveOrder(order);
        return ResponseEntity.ok(updated);
    }
}