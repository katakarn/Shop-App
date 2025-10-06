package com.shopapp.product_service.model;

import com.shopapp.product_service.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ตะกร้าสินค้าผูกกับ User 1 คน (One-to-One)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    // รายการสินค้าในตะกร้า (One-to-Many)
    // "mappedBy" ชี้ไปที่ field ใน CartItem ที่ผูกกับ Cart นี้
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    // เราไม่จำเป็นต้องเก็บราคารวมใน Entity นี้ เพราะสามารถคำนวณได้
}