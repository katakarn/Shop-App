package com.shopapp.product_service.dto;

import java.util.List;

public record CartResponse (
    Long cartId,
    Long userId,
    Double totalPrice,
    Integer totalItems,
    List<CartItemDto> items
) {}
