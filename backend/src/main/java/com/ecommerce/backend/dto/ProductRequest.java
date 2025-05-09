package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private int stock;
    private Long categoryId;
}

