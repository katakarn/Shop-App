package com.shopapp.product_service.service;
import com.shopapp.product_service.model.Order;
import com.shopapp.product_service.model.OrderStatus;
import com.shopapp.product_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async; // สำหรับการทำงานใน Thread แยก
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;

    // ต้องเปิดใช้งาน @EnableAsync ใน ShopappBackendApplication.java ก่อน
    @Async
    public void processPayment(Long orderId) {
        // จำลอง Latency ของ Payment Gateway (รอ 1-3 วินาที)
        try {
            long processingTime = (new Random().nextInt(3) + 1) * 1000;
            log.warn("Payment Gateway: Processing payment for Order ID {}... (simulated {}ms delay)", orderId, processingTime);
            Thread.sleep(processingTime);
        } catch (InterruptedException ignored) {
        }

        // จำลองผลลัพธ์: 90% สำเร็จ, 10% ล้มเหลว
        boolean success = new Random().nextDouble() < 0.9;

        // อัปเดตสถานะ Order หลังประมวลผล
        updateOrderStatusAfterPayment(orderId, success);
    }

    @Transactional
    public void updateOrderStatusAfterPayment(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found during payment update"));

        if (success) {
            order.setStatus(OrderStatus.PROCESSING); // เปลี่ยนเป็น PROCESSING
            order.setTransactionId("TXN-" + System.currentTimeMillis());
            log.info("💰 Order ID {} payment SUCCEEDED. Status updated to PROCESSING.", orderId);
        } else {
            // ในกรณีใช้งานจริง จะมีการเปลี่ยนสต็อกกลับ (Rollback Inventory) ด้วย
            order.setStatus(OrderStatus.CANCELLED); // เปลี่ยนเป็น CANCELLED
            log.error("❌ Order ID {} payment FAILED. Status updated to CANCELLED.", orderId);
        }

        orderRepository.save(order);
    }
}
