package com.shopapp.product_service.dto;

// DTO สำหรับการส่งข้อมูลออก (Response)
public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Integer stockQuantity,
        Boolean isInStock // อาจเพิ่ม field ที่คำนวณได้
) {
}