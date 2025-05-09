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
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final CartItemRepository cartItemRepo;
    private final UserRepository userRepo;
    private final PayPalAccessTokenService payPalAccessTokenService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${paypal.api.base}")
    private String paypalBaseUrl;

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

    @PostConstruct
    public void init() {
        System.out.println(">>> stripeSecretKey = " + stripeSecretKey);
    }

    public Payment processPayment(User user, PaymentRequest request) {
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        double expectedAmount = order.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        if (paymentRepo.existsByOrderId(order.getId())) {
            throw new IllegalStateException("Bu sipariş zaten ödenmiş.");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());

        if (request.getAmount() != expectedAmount) {
            payment.setSuccess(false);
            return paymentRepo.save(payment);
        }

        payment.setSuccess(true);
        paymentRepo.save(payment);

        order.setStatus(OrderStatus.PROCESSING);
        orderRepo.save(order);
        cartItemRepo.findByUser(user).forEach(cartItemRepo::delete);

        return payment;
    }

    /**
     * 💳 Stripe PaymentIntent oluşturur ve clientSecret döner
     */
    public Map<String, String> processStripePayment(PaymentRequest request) {
        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (request.getAmount() * 100))
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .setDescription("E-ticaret Stripe Ödemesi")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return Map.of("clientSecret", intent.getClientSecret());

        } catch (StripeException e) {
            throw new RuntimeException("Stripe ödeme hatası: " + e.getMessage());
        }
    }

    public Payment processPayPalPayment(PaymentRequest request) {
        try {
            String accessToken = payPalAccessTokenService.getAccessToken();

            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode amount = mapper.createObjectNode();
            amount.put("currency_code", "USD");
            amount.put("value", String.format("%.2f", request.getAmount()));

            ObjectNode purchaseUnit = mapper.createObjectNode();
            purchaseUnit.set("amount", amount);

            ArrayNode purchaseUnits = mapper.createArrayNode();
            purchaseUnits.add(purchaseUnit);

            ObjectNode payload = mapper.createObjectNode();
            payload.put("intent", "CAPTURE");
            payload.set("purchase_units", purchaseUnits);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(paypalBaseUrl + "/v2/checkout/orders"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("✅ PayPal siparişi oluşturuldu.");
                request.setSuccess(true);
            } else {
                System.out.println("❌ PayPal API hatası: " + response.body());
                request.setSuccess(false);
            }

        } catch (Exception e) {
            System.out.println("❌ PayPal istisnası: " + e.getMessage());
            request.setSuccess(false);
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return processPayment(user, request);
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepo.findById(id).orElse(null);
    }
}
