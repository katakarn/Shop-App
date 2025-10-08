package com.shopapp.product_service.dto;

import java.time.LocalDateTime;

// DTO สำหรับตอบกลับเมื่อเกิดข้อผิดพลาด
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}