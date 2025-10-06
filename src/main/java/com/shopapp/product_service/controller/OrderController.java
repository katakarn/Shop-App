package com.shopapp.product_service.controller;

import com.shopapp.product_service.dto.OrderResponse;
import com.shopapp.product_service.model.Order;
import com.shopapp.product_service.model.user.User;
import com.shopapp.product_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/v1/orders - สั่งซื้อสินค้า
    // (Customer Protected)
//    @PostMapping
//    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal User user) {
//        try {
//            Order newOrder = orderService.placeOrder(user);
//            // ตอบกลับ 201 Created
//            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
//        } catch (RuntimeException e) {
//            // กรณีสต็อกไม่พอ หรือตะกร้าว่าง
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal User user) {
        try {
            OrderResponse newOrder = orderService.placeOrder(user);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // GET /api/v1/orders - ดูประวัติการสั่งซื้อของฉัน
    // (Customer Protected)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User user) {
        List<OrderResponse> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    // GET /api/v1/orders/admin - ดูคำสั่งซื้อทั้งหมด (สำหรับ Admin)
    // (Admin Protected)
    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        // ในโปรเจกต์จริง ควรใช้ OrderRepository.findAll()
        // แต่เพื่อความง่ายในตัวอย่างนี้ ให้ถือว่ามีเมธอดใน Service ที่เรียก findAll()
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}