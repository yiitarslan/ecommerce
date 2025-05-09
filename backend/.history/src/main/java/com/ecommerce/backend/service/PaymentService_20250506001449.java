package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.PaymentRequest;
import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ PaymentService — Stripe ve PayPal ile ödeme işlemlerini yöneten servis sınıfıdır.
 * 
 * Ana görevleri:
 * - Stripe veya PayPal ödeme işlemlerini başlatmak
 * - Ödemeleri veritabanına kaydetmek
 * - Sipariş ve sepet durumunu güncellemek
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final CartItemRepository cartItemRepo;
    private final UserRepository userRepo;
    private final PayPalAccessTokenService payPalAccessTokenService;

    // Stripe API anahtarı
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // PayPal API base URL (sandbox ortamı)
    @Value("${paypal.api.base}")
    private String paypalBaseUrl;

    // Constructor (Dependency Injection)
    public PaymentService(PaymentRepository paymentRepo,
                          OrderRepository orderRepo,
                          CartItemRepository cartItemRepo,
                          UserRepository userRepo,
                          PayPalAccessTokenService payPalAccessTokenService) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.cartItemRepo = cartItemRepo;
        this.userRepo = userRepo;
        this.payPalAccessTokenService = payPalAccessTokenService;
    }

    // Stripe secret key test amaçlı konsola yazılır
    @PostConstruct
    public void init() {
        System.out.println(">>> stripeSecretKey = " + stripeSecretKey);
    }

    /**
     * 🧾 Ortak ödeme işlemini gerçekleştiren metot.
     * - Sipariş doğrulaması yapar
     * - Daha önce ödeme yapılmış mı kontrol eder
     * - Başarılıysa ödeme kaydeder, siparişi işler, sepeti temizler
     */
    public Payment processPayment(User user, PaymentRequest request) {
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        // Siparişin beklenen toplam tutarını hesapla
        double expectedAmount = order.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        // Sipariş daha önce ödenmişse hata fırlat
        if (paymentRepo.existsByOrderId(order.getId())) {
            throw new IllegalStateException("Bu sipariş zaten ödenmiş.");
        }

        // Ödeme nesnesini oluştur
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());

        // Ödenen tutar ile siparişin beklenen tutarı uyuşmuyorsa başarısız işaretle
        if (request.getAmount() != expectedAmount) {
            payment.setSuccess(false);
            return paymentRepo.save(payment);
        }

        // Başarılı ödeme → kaydet, siparişi güncelle, sepeti temizle
        payment.setSuccess(true);
        paymentRepo.save(payment);

        order.setStatus(OrderStatus.PROCESSING);
        orderRepo.save(order);
        cartItemRepo.findByUser(user).forEach(cartItemRepo::delete);

        return payment;
    }

    /**
     * 💳 Stripe ile ödeme işlemi
     * - Stripe PaymentIntent API kullanılarak ödeme başlatılır
     */
    public Payment processStripePayment(PaymentRequest request) {
        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (request.getAmount() * 100))  // USD → cent
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            System.out.println("✅ Stripe PaymentIntent ID: " + intent.getId());
            request.setSuccess(true);

        } catch (StripeException e) {
            System.out.println("❌ Stripe ödeme hatası: " + e.getMessage());
            request.setSuccess(false);
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return processPayment(user, request);
    }

    /**
     * 💼 PayPal (gerçek sandbox) ile ödeme işlemi
     * - Access token alınır
     * - Sipariş oluşturma isteği PayPal API'ye gönderilir
     */
    public Payment processPayPalPayment(PaymentRequest request) {
        try {
            // PayPal'dan access token al
            String accessToken = payPalAccessTokenService.getAccessToken();

            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            // Ödeme tutarı JSON objesi
            ObjectNode amount = mapper.createObjectNode();
            amount.put("currency_code", "USD");
            amount.put("value", String.format("%.2f", request.getAmount()));

            // PurchaseUnit objesi oluştur
            ObjectNode purchaseUnit = mapper.createObjectNode();
            purchaseUnit.set("amount", amount);

            ArrayNode purchaseUnits = mapper.createArrayNode();
            purchaseUnits.add(purchaseUnit);

            // PayPal istek payload’u
            ObjectNode payload = mapper.createObjectNode();
            payload.put("intent", "CAPTURE");
            payload.set("purchase_units", purchaseUnits);

            // HTTP isteğini oluştur
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(paypalBaseUrl + "/v2/checkout/orders"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            // İstek gönder ve yanıtı işle
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("✅ PayPal siparişi başarıyla oluşturuldu.");
                request.setSuccess(true);
            } else {
                System.out.println("❌ PayPal API hatası: " + response.body());
                request.setSuccess(false);
            }

        } catch (Exception e) {
            System.out.println("❌ PayPal ödeme istisnası: " + e.getMessage());
            request.setSuccess(false);
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return processPayment(user, request);
    }

    /**
     * 📃 Tüm ödemeleri getir
     */
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    /**
     * 🔍 ID'ye göre tek bir ödeme kaydı getir
     */
    public Payment getPaymentById(Long id) {
        return paymentRepo.findById(id).orElse(null);
    }
}
