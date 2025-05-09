package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.service.AdminService;
import com.ecommerce.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService; // ✅ eklendi

    // Tüm kullanıcıları getir
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    // Kullanıcı sil
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Kullanıcı silindi");
    }

    // Kullanıcıyı banla
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return ResponseEntity.ok("Kullanıcı banlandı");
    }

    // ✅ Admin tüm ürünleri listeleyebilsin
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
    productService.deleteProductByAdmin(id); // yeni metod
    return ResponseEntity.ok("Ürün silindi");
}
}
