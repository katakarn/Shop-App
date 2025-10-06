package com.shopapp.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopapp.product_service.dto.ProductRequest;
import com.shopapp.product_service.model.Product;
import com.shopapp.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional; // ใช้เพื่อให้ทุก Test Rollback

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc // สำหรับใช้ MockMvc
@Transactional // ทุก test จะถูก rollback หลังจบการทำงาน (ไม่กระทบ DB จริง)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // สำหรับจำลองการเรียก API

    @Autowired
    private ObjectMapper objectMapper; // สำหรับแปลง Object เป็น JSON

    @Autowired
    private ProductRepository productRepository; // ใช้ในการ setup ข้อมูล

    // Helper: จำลอง User ที่มี Role เป็น ADMIN (เนื่องจาก POST ต้องใช้ ADMIN)
    // *** หมายเหตุ: ในโปรเจกต์จริง ต้องมีการตั้งค่า Spring Security Test Utility เพื่อจำลอง Token/User ***
    // โค้ดด้านล่างนี้เป็นเพียงตัวอย่างสำหรับฟังก์ชันการเรียก POST

    @Test
    void contextLoads() {
        // ตรวจสอบว่า Context ของ Spring Boot ถูกโหลดขึ้นมาได้หรือไม่
    }

    @Test
    void getProductById_success() throws Exception {
        // Arrange
        var saved = productRepository.save(
                new Product(null, "Existing Product", "Existing Description", 300.0, 20)
        );

        // Act + Assert
        mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Product"))
                .andExpect(jsonPath("$.price").value(300.0));
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    void createProduct_success() throws Exception {
        var body = new ProductRequest("New Product", "New Description", 400.0, 30);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(400.0));
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    void shouldReturnBadRequest_WhenPriceIsNegative() throws Exception {
        // 1. เตรียมข้อมูล Request ที่ผิด (Validation Failure)
        var invalidRequest = new ProductRequest(
                "Bad Product", "Test Description", -10.0, 50
        );

        // 2. จำลองการเรียก POST API
        mockMvc.perform(post("/api/v1/products")
                        // .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))

                // 3. ตรวจสอบผลลัพธ์
                .andExpect(status().isBadRequest()) // คาดหวัง HTTP 400 Bad Request
                .andExpect(jsonPath("$.price").exists()); // ตรวจสอบว่ามีการส่ง Error ของ field price กลับมา
    }
}