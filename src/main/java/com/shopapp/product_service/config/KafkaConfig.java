package com.shopapp.product_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConfig {

    // Spring Boot มักจะสร้าง KafkaTemplate และ ConsumerFactory ให้โดยอัตโนมัติ
    // แต่เราสามารถสร้าง ContainerFactory เพื่อกำหนดค่าเฉพาะได้

    // @Bean
    // public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
    //         ConsumerFactory<String, Object> consumerFactory) {
    //     ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(consumerFactory);
    //     // factory.setConcurrency(3); // ตัวอย่างการตั้งค่า Concurrency
    //     return factory;
    // }

    // ในการตั้งค่าพื้นฐานของ Spring Boot มักไม่จำเป็นต้องสร้าง Bean นี้ ถ้ามีการตั้งค่าใน application.properties ชัดเจนแล้ว
    // โค้ดนี้ถูกคอมเมนต์ไว้เพื่อให้ Spring Boot Auto-Configuration จัดการ เพื่อความง่าย
}