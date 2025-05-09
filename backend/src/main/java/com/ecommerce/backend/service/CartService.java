package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.AddToCartRequest;
import com.ecommerce.backend.dto.CartItemResponseDTO;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ CartService — Kullanıcının sepet işlemlerini yöneten servis sınıfıdır.
 * - Ürün ekleme / çıkarma
 * - Sepeti listeleme ve dönüştürme
 * - Miktar güncelleme
 * - Sepette ürün var mı kontrolü
 */
@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartItemRepo,
                       ProductRepository productRepo) {
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
    }

    /**
     * 1️⃣ Sepete ürün ekler veya varsa miktarını artırır.
     */
    public CartItem addToCart(User user, AddToCartRequest req) {
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        CartItem existing = cartItemRepo.findByUserAndProductId(user, product.getId()).orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
            existing.setTotalPrice(existing.getProduct().getPrice() * existing.getQuantity());
            return cartItemRepo.save(existing);
        }

        CartItem newItem = new CartItem(user, product, req.getQuantity());
        return cartItemRepo.save(newItem);
    }

    /**
     * 2️⃣ Kullanıcının sepetindeki tüm ürünleri entity olarak döner.
     */
    public List<CartItem> getCartItems(User user) {
        return cartItemRepo.findByUser(user);
    }

    /**
     * 3️⃣ Kullanıcının sepet öğelerini DTO formatında döner.
     */
    public List<CartItemResponseDTO> getCartItemDTOs(User user) {
        List<CartItem> items = cartItemRepo.findByUser(user);
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4️⃣ Tek bir CartItem nesnesini DTO’ya dönüştürür.
     */
    public CartItemResponseDTO convertToDTO(CartItem item) {
        return new CartItemResponseDTO(
                item.getId(),
                item.getQuantity(),
                item.getTotalPrice(),
                item.getProduct(),
                item.getUser().getId()
        );
    }

    /**
     * 5️⃣ Sepetten ürün çıkarır.
     */
    public void removeFromCart(Long itemId) {
        cartItemRepo.deleteById(itemId);
    }

    /**
     * 6️⃣ Sepetin toplam fiyatını hesaplar.
     */
    public double getCartTotal(User user) {
        List<CartItem> items = cartItemRepo.findByUser(user);
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * 7️⃣ Sepetteki ürün miktarını ID üzerinden günceller.
     */
    public CartItem updateCartItemQuantity(Long itemId, int newQuantity) {
        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sepet öğesi bulunamadı"));

        item.setQuantity(newQuantity);
        item.setTotalPrice(item.getProduct().getPrice() * newQuantity);
        return cartItemRepo.save(item);
    }

    /**
     * 8️⃣ Sepet güncellemesinde kullanıcı doğrulaması içerir.
     */
    public CartItem updateQuantity(User user, Long itemId, int quantity) {
        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sepet öğesi bulunamadı."));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu ürün bu kullanıcıya ait değil.");
        }

        item.setQuantity(quantity);
        item.setTotalPrice(item.getProduct().getPrice() * quantity);
        return cartItemRepo.save(item);
    }

    /**
     * 9️⃣ Sepette belirli ürün var mı kontrol eder.
     * Kullanıcının sepetindeki ürünlerden herhangi biri verilen ürün adına eşleşiyorsa true döner.
     * @param user kullanıcı
     * @param productName ürün adı
     * @return sepette ürün varsa true, yoksa false
     */
    public boolean cartContainsProduct(User user, String productName) {
        List<CartItem> items = cartItemRepo.findByUser(user);
        return items.stream()
                .anyMatch(item -> item.getProduct().getName().equalsIgnoreCase(productName));
    }
}
