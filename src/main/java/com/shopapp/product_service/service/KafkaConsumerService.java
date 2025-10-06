package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.OrderNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private static final String ORDER_TOPIC = "order-notifications";
    private static final String GROUP_ID = "notification-group";

    @KafkaListener(topics = ORDER_TOPIC, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void listen(OrderNotification notification) {
        log.info("✅ Notification Service received Order ID: {}", notification.orderId());
        log.info("Total Amount: {}", notification.totalAmount());

        // *** นี่คือจุดที่คุณสามารถเพิ่ม Business Logic Asynchronous ***
        // เช่น:
        // 1. sendEmail(notification.getUserId(), "Order Confirmation");
        // 2. updateLogisticsSystem(notification.getOrderId());

        log.info("Processing complete for Order ID: {}", notification.orderId());
    }
}