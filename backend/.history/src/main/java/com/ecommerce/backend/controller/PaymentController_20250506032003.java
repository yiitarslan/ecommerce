package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.PaymentRequest;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest request) {
        if (request.getUserId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Kullanıcı ID ve Sipariş ID boş olamaz.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Payment saved = paymentService.processPayment(user, request);
        return ResponseEntity.ok(saved);
    }

    /**
     * Stripe PaymentIntent oluşturur → clientSecret döner
     */
    @PostMapping("/stripe")
    public ResponseEntity<?> makeStripePayment(@RequestBody PaymentRequest request) {
        if (request.getUserId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Kullanıcı ID ve Sipariş ID boş olamaz.");
        }

        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        try {
            Map<String, String> result = paymentService.processStripePayment(request);
            return ResponseEntity.ok(result); // 🔥 clientSecret frontend'e gidiyor
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/paypal")
    public ResponseEntity<?> makePaypalPayment(@RequestBody PaymentRequest request) {
        if (request.getUserId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Kullanıcı ID ve Sipariş ID boş olamaz.");
        }

        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        try {
            Payment saved = paymentService.processPayPalPayment(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}
