package com.shopapp.product_service.dto;

public record OrderResponse(
        Long orderId,
        String orderDate,
        String status,
        double totalAmount
) {
}
