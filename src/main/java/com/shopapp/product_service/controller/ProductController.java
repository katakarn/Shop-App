package com.shopapp.product_service.controller;

import com.shopapp.product_service.dto.ProductRequest;
import com.shopapp.product_service.dto.ProductResponse;
import com.shopapp.product_service.model.Product;
import com.shopapp.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products") // กำหนด Base URL
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }

    // Endpoint: GET /api/v1/products
    // สำหรับดึงสินค้าทั้งหมด
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProduct();
        return ResponseEntity.ok(products);
    }

    // Endpoint: GET /api/v1/products/{id}
    // สำหรับดึงสินค้าตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService
                .getProductResponseById(id)
                .map(ResponseEntity::ok) // ถ้าเจอสินค้า ให้ส่งโค้ด 200 OK
                .orElse(ResponseEntity.notFound().build()); // ถ้าไม่เจอ ให้ส่งโค้ด 404 Not Found
    }

    // Endpoint: POST /api/v1/products
    // สำหรับสร้างสินค้าใหม่
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        // การใส่ @Valid จะทำให้ Spring ตรวจสอบตาม Annotation ใน Product.java ก่อนเข้า Method นี้
        ProductResponse newProduct = productService.createProduct(request);
        // ตอบกลับด้วยโค้ด 201 Created
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    // Endpoint: PUT /api/v1/products/{id}
    // สำหรับแก้ไขสินค้า
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct); // โค้ด 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // ถ้าไม่เจอ ให้ส่งโค้ด 404 Not Found
        }
    }

    // Endpoint: DELETE /api/v1/products/{id}
    // สำหรับลบสินค้า
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // โค้ด 204 No Content (สำเร็จแต่ไม่มีข้อมูลส่งกลับ)
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // ถ้าไม่เจอ ให้ส่งโค้ด 404 Not Found
        }
    }

}