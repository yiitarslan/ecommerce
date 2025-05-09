package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductRequest;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ Ürün ekle (JWT ile seller'e göre)
    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String jwt = token.substring(7);
            String email = jwtTokenProvider.extractEmail(jwt);  // 🔧 burada düzeltme yapıldı
            User seller = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategori bulunamadı"));

            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setImageUrl(request.getImageUrl());
            product.setPrice(request.getPrice());
            product.setStock(request.getStock());
            product.setCategory(category);
            product.setSeller(seller);

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryName}")
    public List<Product> getByCategory(@PathVariable String categoryName) {
        return productRepository.findByCategory_Name(categoryName);
    }
}
