package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.*;
import com.ecommerce.backend.security.JwtTokenProvider;
import com.ecommerce.backend.service.OrderService;
import com.stripe.Stripe;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public OrderController(OrderService orderService,
                           UserRepository userRepository,
                           JwtTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Order order = orderService.createOrderFromCart(user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        List<Order> orders = orderService.getAllOrders(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<Order>> getPendingOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        List<Order> pendingOrders = orderService.getOrdersByStatus(user, OrderStatus.PENDING);
        return ResponseEntity.ok(pendingOrders);
    }

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

    @GetMapping("/user")
    public List<Order> getUserOrders(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return orderService.getAllOrders(user);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrdersForAdmin() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    /**
     * Admin siparişi tamamen siler (ve varsa Stripe refund yapar)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Sipariş bulunamadı."));
        }

        Order order = optionalOrder.get();

        Optional<Payment> optionalPayment = paymentRepository.findByOrder(order);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            String paymentIntentId = payment.getPaymentIntentId();

            if (paymentIntentId != null && !paymentIntentId.isBlank()) {
                try {
                    Stripe.apiKey = stripeSecretKey;

                    RefundCreateParams params = RefundCreateParams.builder()
                            .setPaymentIntent(paymentIntentId)
                            .build();

                    Refund.create(params);
                    System.out.println("✅ Refund işlemi başarılı: " + paymentIntentId);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Refund işlemi başarısız: " + e.getMessage()));
                }
            }

            // ödeme kaydını da sil
            paymentRepository.delete(payment);
        }

        // siparişi sil
        orderRepository.delete(order);

        return ResponseEntity.ok(Map.of("message", "Sipariş ve ödeme kaydı silindi, refund yapıldı."));
    }

    /**
     * Kullanıcı kendi siparişini iptal eder (durum: CANCELLED)
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Sipariş bulunamadı."));
        }

        Order order = optionalOrder.get();

        if (!order.getStatus().equals(OrderStatus.PROCESSING)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Yalnızca PROCESSING durumundaki siparişler iptal edilebilir."));
        }

        Optional<Payment> optionalPayment = paymentRepository.findByOrder(order);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            String paymentIntentId = payment.getPaymentIntentId();

            if (paymentIntentId != null && !paymentIntentId.isBlank()) {
                try {
                    Stripe.apiKey = stripeSecretKey;

                    RefundCreateParams params = RefundCreateParams.builder()
                            .setPaymentIntent(paymentIntentId)
                            .build();

                    Refund.create(params);
                    System.out.println("✅ Refund işlemi başarılı (iptal): " + paymentIntentId);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Stripe refund işlemi başarısız: " + e.getMessage()));
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of("message", "Sipariş iptal edildi ve ödeme iade edildi."));
    }
}
