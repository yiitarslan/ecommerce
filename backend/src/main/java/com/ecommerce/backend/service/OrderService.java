package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository; // ✅ EKLENDİ

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        PaymentRepository paymentRepository) { // ✅ CONSTRUCTOR'A EKLENDİ
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentRepository = paymentRepository;
    }

    public Order createOrderFromCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Sepet boş, sipariş oluşturulamaz.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setProductName(cartItem.getProduct().getName());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getProduct().getPrice());
            item.setOrder(null);
            orderItems.add(item);
            totalPrice += item.getTotalPrice();
        }

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(user.getFullName());
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        Order saved = orderRepository.save(order);
        saved.setStatus(OrderStatus.PENDING);
        return orderRepository.save(saved);
    }

    public List<Order> getAllOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id, User user) {
        return orderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı."));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı."));
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAll();
    }

    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        // ✅ İlişkili ödeme varsa sil
        paymentRepository.findByOrder(order).ifPresent(paymentRepository::delete);

        // ✅ Siparişi sil
        orderRepository.delete(order);
    }
}
