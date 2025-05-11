package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.AdminAnalyticsResponse;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    private final OrderService orderService;

    public AdminAnalyticsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public AdminAnalyticsResponse getAnalytics() {
        List<Order> allOrders = orderService.getAllOrdersForAdmin()
        .stream()
        .filter(order -> !order.getStatus().name().equalsIgnoreCase("CANCELLED"))
        .toList();


        long totalOrders = allOrders.size();
        double totalRevenue = allOrders.stream().mapToDouble(Order::getTotalPrice).sum();

        Map<String, Integer> productCountMap = new HashMap<>();
        Map<String, Integer> customerOrderMap = new HashMap<>();

        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                productCountMap.put(item.getProductName(),
                        productCountMap.getOrDefault(item.getProductName(), 0) + item.getQuantity());
            }

            String name = order.getCustomerName();
            customerOrderMap.put(name, customerOrderMap.getOrDefault(name, 0) + 1);
        }

        String topProduct = productCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Yok");

        String topCustomer = customerOrderMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Yok");

        return new AdminAnalyticsResponse(
                totalOrders,
                totalRevenue,
                topProduct,
                topCustomer,
                productCountMap,
                customerOrderMap
        );
    }
}
