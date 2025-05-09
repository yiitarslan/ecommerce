package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.AddToCartRequest;
import com.ecommerce.backend.dto.CartItemResponseDTO;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ✅ CartController — Kullanıcının sepetteki işlemlerini yöneten REST controller.
 * İşlevler:
 * - Ürün ekleme, çıkarma, görüntüleme
 * - Miktar güncelleme ve sepet toplamı hesaplama
 * - Belirli ürün sepette var mı kontrolü (opsiyonel özellik)
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    /**
     * 1️⃣ Sepete ürün ekler.
     * Endpoint: POST /cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<CartItemResponseDTO> addToCart(@RequestBody AddToCartRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        CartItem item = cartService.addToCart(user, req);
        return ResponseEntity.ok(cartService.convertToDTO(item));
    }

    /**
     * 2️⃣ Sepetten ürün siler.
     * Endpoint: DELETE /cart/remove/{itemId}
     */
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 3️⃣ Kullanıcının sepetini görüntüler.
     * Endpoint: GET /cart/view/{userId}
     */
    @GetMapping("/view/{userId}")
    public ResponseEntity<List<CartItemResponseDTO>> viewCartByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return ResponseEntity.ok(cartService.getCartItemDTOs(user));
    }

    /**
     * 4️⃣ Sepetin toplam fiyatını hesaplar.
     * Endpoint: POST /cart/checkout/{userId}
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<String> calculateCartTotal(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        double total = cartService.getCartTotal(user);
        return ResponseEntity.ok("Sepet toplamı: " + total);
    }

    /**
     * 5️⃣ Sepetteki ürün miktarını günceller.
     * Endpoint: PUT /cart/update/{itemId}
     */
    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItemResponseDTO> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> body) {

        Long userId = Long.valueOf(body.get("userId").toString());
        int quantity = (int) body.get("quantity");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        CartItem updatedItem = cartService.updateQuantity(user, itemId, quantity);
        return ResponseEntity.ok(cartService.convertToDTO(updatedItem));
    }

    /**
     * 6️⃣ Sepette belirli ürün var mı kontrol eder.
     * Endpoint: GET /cart/contains/{userId}/{productName}
     * @return true → ürün sepette varsa, false → yoksa
     */
    @GetMapping("/contains/{userId}/{productName}")
    public ResponseEntity<Boolean> cartContainsProduct(@PathVariable Long userId,
                                                       @PathVariable String productName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        boolean exists = cartService.cartContainsProduct(user, productName);
        return ResponseEntity.ok(exists);
    }
}
