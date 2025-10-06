package com.shopapp.product_service.dto;

import jakarta.validation.constraints.*;

// ใช้ record เพื่อลด boilerplate code (Java 16+)
public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Name is too long")
        String name,

        @Size(max = 200, message = "Description is too long")
        String description,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be positive or zero")
        Double price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity cannot be negative")
        Integer stockQuantity
) {
    // ไม่มีเนื้อหาเพิ่มเติม เนื่องจาก record จัดการ Getter/Constructor ให้แล้ว
}