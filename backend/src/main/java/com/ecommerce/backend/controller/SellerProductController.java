package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductRequest;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.security.JwtTokenProvider;
import com.ecommerce.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@CrossOrigin(origins = "http://localhost:4200")
public class SellerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String getEmailFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtTokenProvider.extractEmail(authHeader.substring(7));
        }
        throw new RuntimeException("Geçersiz JWT Token");
    }

    @GetMapping
    public List<Product> getSellerProducts(@RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromHeader(authHeader);
        return productService.getProductsBySeller(email);
    }

    @PostMapping
    public Product addProduct(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProductRequest request
    ) {
        String email = getEmailFromHeader(authHeader);
        return productService.addProduct(email, request);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProductRequest request
    ) throws Exception {
        String email = getEmailFromHeader(authHeader);
        return productService.updateProduct(id, email, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        String email = getEmailFromHeader(authHeader);
        productService.deleteProduct(id, email);
    }

    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String email = getEmailFromHeader(authHeader);
        return productService.getProductById(id, email);
    }
}
