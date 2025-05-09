package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.ecommerce.backend.model.Order;



@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
    Optional<Payment> findByOrder(Order order);
}
