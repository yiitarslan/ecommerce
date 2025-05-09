package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Name(String name);
    List<Product> findBySellerId(Long sellerId);
}
