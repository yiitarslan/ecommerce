package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository; // ✅ Eksik olan bu satır

    // Tüm kullanıcıları getir
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

    // 1. Bu kullanıcıya ait ürünleri tek tek sil
        List<Product> sellerProducts = productRepository.findBySellerId(id);
            for (Product product : sellerProducts) {
                productRepository.delete(product);
    }

    // 2. Son olarak kullanıcıyı sil
    userRepository.delete(user);
}

    // Kullanıcıyı banla
    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.setRole("BANNED");
        userRepository.save(user);
    }
}
