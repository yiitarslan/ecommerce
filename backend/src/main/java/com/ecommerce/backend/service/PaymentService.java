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
import java.util.Optional;

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
        Stripe.apiKey = stripeSecretKey;
    }

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
            return Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "paymentIntentId", intent.getId()
            );

        } catch (StripeException e) {
            throw new RuntimeException("Stripe ödeme hatası: " + e.getMessage());
        }
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
        payment.setPaymentIntentId(request.getPaymentIntentId());

        if (request.getSuccess() != null && !request.getSuccess()) {
            payment.setSuccess(false);
            return paymentRepo.save(payment);
        }

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

            request.setSuccess(response.statusCode() == 201);

        } catch (Exception e) {
            request.setSuccess(false);
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return processPayment(user, request);
    }

    public void refundStripePayment(Long paymentId) throws StripeException {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı"));

        if (!payment.isSuccess()) {
            throw new RuntimeException("Sadece başarılı ödemeler iade edilebilir.");
        }

        if (payment.getPaymentIntentId() == null) {
            throw new RuntimeException("paymentIntentId mevcut değil.");
        }

        Stripe.apiKey = stripeSecretKey;
        Map<String, Object> params = Map.of("payment_intent", payment.getPaymentIntentId());
        com.stripe.model.Refund.create(params);

        payment.setSuccess(false);
        paymentRepo.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepo.findById(id).orElse(null);
    }

    public Optional<Payment> getPaymentByOrderId(Long orderId) {
    return paymentRepo.findByOrderId(orderId);
}
}
