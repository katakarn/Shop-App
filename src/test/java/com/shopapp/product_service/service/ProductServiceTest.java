package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.ProductRequest;
import com.shopapp.product_service.model.Product;
import com.shopapp.product_service.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // เปิดใช้งาน Mockito
class ProductServiceTest {

    @Mock // จำลอง ProductRepository (ไม่ต้องเรียก DB จริง)
    private ProductRepository productRepository;

    @InjectMocks // ฉีด ProductRepository ที่ Mock เข้าไปใน ProductService ที่เราจะทดสอบ
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
        // รันก่อนทุกๆ Test Case
    void setUp() {
        product = new Product(1L, "Laptop Test", "High-performance laptop", 1500.0, 10);

        productRequest = new ProductRequest(
                "New Test Product", "Description", 100.0, 5
        );
    }

    @Test
    void whenCreateProduct_thenReturnProductResponse() {
        // กำหนดพฤติกรรมของ Mock: เมื่อเรียก save() ให้คืนค่า product เดิม
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // ดำเนินการทดสอบ
        var response = productService.createProduct(productRequest);

        // ตรวจสอบผลลัพธ์
        assertNotNull(response);
        assertEquals("Laptop Test", response.name());
        verify(productRepository, times(1)).save(any(Product.class)); // ตรวจสอบว่า save ถูกเรียก 1 ครั้ง
    }

    @Test
    void whenGetProductById_thenReturnsProductResponse() {
        // กำหนดพฤติกรรมของ Mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // ดำเนินการทดสอบ
        var found = productService.getProductResponseById(1L);

        // ตรวจสอบผลลัพธ์
        assertNotNull(found);
        assertEquals("Laptop Test", found.name());
    }

    @Test
    void whenUpdateProduct_thenThrowsExceptionForNotFound() {
        // กำหนดพฤติกรรมของ Mock: เมื่อหา ID 99L จะไม่พบ
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // ตรวจสอบว่าเมธอดโยน Exception ตามที่คาดไว้หรือไม่
        assertThrows(RuntimeException.class, () -> productService.updateProduct(99L, productRequest));
    }
}