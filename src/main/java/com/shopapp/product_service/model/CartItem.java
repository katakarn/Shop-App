package com.shopapp.product_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // รายการสินค้าผูกกับตะกร้า 1 อัน (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY เพื่อไม่ดึง Cart มาทุกครั้ง
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // รายการสินค้าผูกกับ Product 1 ชิ้น (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity; // จำนวนสินค้า
}