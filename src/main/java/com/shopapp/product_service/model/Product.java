package com.shopapp.product_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products") // กำหนดชื่อตาราง
@Data // Lombok: สร้าง Getter, Setter, toString, equals, hashCode อัตโนมัติ
@NoArgsConstructor // Lombok: สร้าง constructor ที่ไม่มี argument
@AllArgsConstructor // Lombok: สร้าง constructor ที่มีทุก field
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @NotBlank(message = "Product name is required") // ห้ามเป็นค่าว่าง
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive or zero") // ราคาต้องมากกว่าหรือเท่ากับ 0
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative") // จำนวนสต็อกห้ามติดลบ
    private Integer stockQuantity;// จำนวนสินค้าในสต็อก
}