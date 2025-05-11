package com.ecommerce.backend.dto;

import java.util.Map;

public class AdminAnalyticsResponse {
    private long totalOrders;
    private double totalRevenue;
    private String topProduct;
    private String topCustomer;

    private Map<String, Integer> productSales;
    private Map<String, Integer> customerOrders;

    public AdminAnalyticsResponse() {}

    public AdminAnalyticsResponse(long totalOrders, double totalRevenue, String topProduct, String topCustomer,
                                  Map<String, Integer> productSales, Map<String, Integer> customerOrders) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.topProduct = topProduct;
        this.topCustomer = topCustomer;
        this.productSales = productSales;
        this.customerOrders = customerOrders;
    }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public String getTopProduct() { return topProduct; }
    public void setTopProduct(String topProduct) { this.topProduct = topProduct; }

    public String getTopCustomer() { return topCustomer; }
    public void setTopCustomer(String topCustomer) { this.topCustomer = topCustomer; }

    public Map<String, Integer> getProductSales() { return productSales; }
    public void setProductSales(Map<String, Integer> productSales) { this.productSales = productSales; }

    public Map<String, Integer> getCustomerOrders() { return customerOrders; }
    public void setCustomerOrders(Map<String, Integer> customerOrders) { this.customerOrders = customerOrders; }
}
