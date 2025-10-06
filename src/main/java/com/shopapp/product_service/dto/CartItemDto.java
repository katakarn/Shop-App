package com.shopapp.product_service.dto;

public record CartItemDto(
        Long productId,
        String productName,
        Double price,
        Integer quantity,
        Double totalPrice
) {
}