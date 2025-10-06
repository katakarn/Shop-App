package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.OrderNotification;
import com.shopapp.product_service.dto.OrderResponse;
import com.shopapp.product_service.model.*;
import com.shopapp.product_service.model.user.User;
import com.shopapp.product_service.repository.*;
import jakarta.transaction.Transactional; // สำคัญ: ใช้สำหรับควบคุม Transaction
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // เพิ่ม Slf4j สำหรับ Logging
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderDate().toString(),
                order.getStatus().name(),
                order.getTotalAmount()
        );
    }

    // Kafka Zone !!!
    private final KafkaProducerService kafkaProducerService; // เพิ่ม Kafka Producer

    @Transactional // **ใช้ @Transactional เพื่อให้มั่นใจว่าถ้ามีขั้นตอนไหนผิดพลาด ทุกอย่างจะ Rollback**
    public OrderResponse placeOrder(User user) {
        // 1. ดึงตะกร้าสินค้า
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart.");
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. ตรวจสอบสต็อก, สร้าง OrderItem, และหักสต็อก
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();

            // 2.1 ตรวจสอบสต็อก
            if (product.getStockQuantity() < requestedQuantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // 2.2 หักสต็อก (Inventory Management)
            product.setStockQuantity(product.getStockQuantity() - requestedQuantity);
            productRepository.save(product); // บันทึกการอัปเดตสต็อก

            // 2.3 สร้าง OrderItem
            OrderItem orderItem = new OrderItem(
                    null, null, product, requestedQuantity, product.getPrice()
            );
            orderItems.add(orderItem);

            totalAmount += product.getPrice() * requestedQuantity;
        }

        // 3. สร้าง Order หลัก
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);

        // บันทึก Order หลักก่อน
        Order savedOrder = orderRepository.save(order);

        // 4. ผูก OrderItem กับ Order ที่บันทึกแล้ว
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        savedOrder.setItems(orderItems);

        // *** ส่วนใหม่: ส่ง Notification ไปยัง Kafka ***
        OrderNotification notification = new OrderNotification(
                savedOrder.getId(),
                user.getId(),
                savedOrder.getTotalAmount(),
                LocalDateTime.now()
        );
        kafkaProducerService.sendOrderNotification(notification);
        // ---------------------------------------------

        // 5. ล้างตะกร้าสินค้า
        cartItems.clear(); // การเคลียร์รายการใน List ที่ผูกกับ CascadeType.ALL จะลบรายการใน DB ด้วย
        cartService.getOrCreateCart(user); // บันทึกการเปลี่ยนแปลงตะกร้า
        orderRepository.save(savedOrder); // บันทึก Order อีกครั้งพร้อม OrderItems
        //cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    // 6. ดึง Order ทั้งหมด/ตาม ID ของ User
    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUser(user).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // สำหรับ Admin: ดึง Order ทั้งหมด
    @Transactional()
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

}