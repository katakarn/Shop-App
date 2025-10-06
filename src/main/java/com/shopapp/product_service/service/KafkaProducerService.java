package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.OrderNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    // กำหนดชื่อ Topic
    private static final String ORDER_TOPIC = "order-notifications";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderNotification(OrderNotification notification) {
        log.info("Producing notification for Order ID: {}", notification.orderId());

        // ส่ง Message ไปยัง Kafka Topic โดยใช้ Order ID เป็น Key
        kafkaTemplate.send(ORDER_TOPIC, notification.orderId().toString(), notification)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message to topic: {} with offset: {}", ORDER_TOPIC, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message: {}", ex.getMessage());
                    }
                });
    }
}