package com.ecommerce.backend.service;

import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ OrderService — Sipariş işlemlerini yöneten servis katmanı.
 * - Sipariş oluşturma
 * - Sipariş görüntüleme
 * - Sipariş durumunu güncelleme işlemlerini içerir.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * 1️⃣ Sepetteki ürünlerle sipariş oluşturur.
     * - Sepet boşsa hata fırlatır.
     * - Ürünler `OrderItem` nesnesine dönüştürülüp yeni siparişe eklenir.
     * - `OrderStatus` varsayılan olarak `PENDING` atanır.
     */
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
            item.setOrder(null); // geçici olarak null, sonra set edilir
            orderItems.add(item);
            totalPrice += item.getTotalPrice();
        }

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(user.getFullName());
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING); // 🔁 STATUS = PENDING

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    /**
     * 2️⃣ Kullanıcının tüm sipariş geçmişini getirir.
     */
    public List<Order> getAllOrders(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * 3️⃣ Kullanıcının belirli bir siparişini döner.
     * Sadece o kullanıcıya aitse döner, aksi halde hata verir.
     */
    public Order getOrderById(Long id, User user) {
        return orderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı."));
    }

    /**
     * 4️⃣ (Admin için) Siparişi ID ile getirir. Kullanıcı kontrolü yapılmaz.
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı."));
    }

    /**
     * 5️⃣ Siparişi günceller (örn: status değişikliği, adres güncellemesi, vs.).
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * 6️⃣ Belirli bir kullanıcıya ait ve belirli bir duruma sahip siparişleri getirir.
     * Örn: PENDING siparişleri listelemek için.
     */
    public List<Order> getOrdersByStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }
}