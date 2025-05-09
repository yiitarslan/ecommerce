package com.ecommerce.backend.model;

/**
 * Siparişin güncel durumunu temsil eden enum sınıfı.
 */
public enum OrderStatus {
    PENDING,        // Sipariş alındı ama işlenmedi
    PROCESSING,     // Sipariş hazırlanıyor
    SHIPPED,        // Kargoya verildi
    DELIVERED,      // Teslim edildi
    CANCELLED       // İptal edildi
}
