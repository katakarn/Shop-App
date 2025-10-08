package com.shopapp.product_service.model;

import com.shopapp.product_service.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // การสั่งซื้อผูกกับ User 1 คน (Many-to-One)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items; // รายการสินค้าใน Order

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // สถานะการสั่งซื้อ (PENDING, SHIPPED, DELIVERED, CANCELLED)

    private Double totalAmount; // ราคารวมทั้งหมด

    private LocalDateTime orderDate; // วันที่สั่งซื้อ

    private String transactionId;
}