package com.shopapp.product_service.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}