package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ProductRequest;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    public List<Product> getProductsBySeller(String email) {
        return userRepo.findByEmail(email)
                .map(user -> productRepo.findBySellerId(user.getId()))
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
    }

    public Product addProduct(String email, ProductRequest request) {
        User seller = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setSeller(seller);

        return productRepo.save(product);
    }

    public Product updateProduct(Long id, String email, ProductRequest request) throws AccessDeniedException {
        Product existing = productRepo.findById(id).orElseThrow();

        if (!existing.getSeller().getEmail().equalsIgnoreCase(email.trim())) {
            throw new AccessDeniedException("Yetkisiz işlem");
        }

        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + request.getCategoryId()));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setImageUrl(request.getImageUrl());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(category);

        return productRepo.save(existing);
    }

    public void deleteProduct(Long id, String email) throws AccessDeniedException {
        Product existing = productRepo.findById(id).orElseThrow();
        if (!existing.getSeller().getEmail().equalsIgnoreCase(email.trim())) {
            throw new AccessDeniedException("Yetkisiz işlem");
        }
        productRepo.deleteById(id);
    }

    public Product getProductById(Long id, String email) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));

        if (!product.getSeller().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Bu ürün bu kullanıcıya ait değil.");
        }

        return product;
    }

    // ✅ Admin için tüm ürünleri getir
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public void deleteProductByAdmin(Long id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Ürün bulunamadı: " + id);
        }
        productRepo.deleteById(id);
    }
}
