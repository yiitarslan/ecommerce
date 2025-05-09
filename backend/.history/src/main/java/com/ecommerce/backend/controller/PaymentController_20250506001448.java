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

/**
 * ✅ PaymentController — Ödeme işlemlerini yöneten REST Controller'dır.
 * 
 * Bu sınıf aşağıdaki işlemleri gerçekleştirir:
 * - Stripe ile ödeme başlatma
 * - PayPal ile ödeme başlatma
 * - Manuel ödeme kayıt etme
 * - Ödeme geçmişi listeleme
 * - Belirli bir ödeme detayını getirme
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    // Constructor: PaymentService ve UserRepository bağımlılıkları enjekte edilir
    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    /**
     * 🧾 1. Genel ödeme kaydı (manuel veya mock kullanım için uygundur)
     * 
     * Endpoint: POST /payments
     * 
     * Request örneği:
     * {
     *   "userId": 1,
     *   "orderId": 10,
     *   "amount": 159.99,
     *   "paymentMethod": "CREDIT_CARD"
     * }
     */
    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest request) {

        // Gerekli alanlar kontrol edilir
        if (request.getUserId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Kullanıcı ID ve Sipariş ID boş olamaz.");
        }

        // Kullanıcı doğrulaması yapılır
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Ödeme işlenir ve kaydedilir
        Payment saved = paymentService.processPayment(user, request);

        // Başarılı yanıt döndürülür
        return ResponseEntity.ok(saved);
    }

    /**
     * 💳 2. Stripe ile gerçek ödeme işlemi başlatılır.
     * 
     * Endpoint: POST /payments/stripe
     * 
     * Stripe ile PaymentIntent oluşturulur.
     * Aynı siparişe birden fazla ödeme yapılması engellenir.
     */
    @PostMapping("/stripe")
    public ResponseEntity<?> makeStripePayment(@RequestBody PaymentRequest request) {

        if (request.getUserId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Kullanıcı ID ve Sipariş ID boş olamaz.");
        }

        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        try {
            Payment saved = paymentService.processStripePayment(request);
            return ResponseEntity.ok(saved);

        } catch (IllegalStateException ise) {
            // Aynı siparişe tekrar ödeme yapılırsa
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ise.getMessage()));
        }
    }

    /**
     * 💼 3. PayPal ile gerçek ödeme işlemi başlatılır.
     * 
     * Endpoint: POST /payments/paypal
     * 
     * PayPal sandbox API ile sipariş oluşturulur.
     * PaymentIntent benzeri yapı ile ödeme kaydı yapılır.
     */
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

    /**
     * 📃 4. Tüm ödemeleri getir (admin paneli için uygundur)
     * 
     * Endpoint: GET /payments
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * 🔍 5. ID ile tek bir ödeme kaydını getir
     * 
     * Endpoint: GET /payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(payment);
    }
}