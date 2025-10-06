package com.shopapp.product_service.dto;

import java.time.LocalDateTime;

// DTO สำหรับข้อมูลการแจ้งเตือน
public record OrderNotification(
        Long orderId,
        Long userId,
        Double totalAmount,
        LocalDateTime timestamp
) {}