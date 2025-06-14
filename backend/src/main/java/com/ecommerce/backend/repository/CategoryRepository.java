package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name); // kolay erişim için
}
