package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.ProductRequest;
import com.shopapp.product_service.dto.ProductResponse;
import com.shopapp.product_service.model.Product;
import com.shopapp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.tool.schema.spi.JpaTargetAndSourceDescriptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

//    // ใช้ @RequiredArgsConstructor จาก Lombok แทนการเขียน Constructor เอง
//    // Dependency Injection ผ่าน Constructor
//    public ProductService(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }

    // *** เมธอดใหม่: แปลง Entity ไปเป็น Response DTO ***
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStockQuantity() > 0 // เพิ่ม Business Logic ใน DTO
        );
    }

    // *** เมธอดใหม่: แปลง Request DTO ไปเป็น Entity (สำหรับ Create) ***
    private Product mapToProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        return product;
    }

//    // CREATE: บันทึกสินค้าใหม่
//    public Product createProduct(Product product) {
//        // อาจเพิ่ม Business Logic เช่น การตรวจสอบ (Validation) ก่อนบันทึก
//        return productRepository.save(product);
//    }
    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapToProduct(request);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

//    // READ: ดึงสินค้าทั้งหมด
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
    public List<ProductResponse> getAllProduct() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList(); // แปลง List<Product> เป็น List<ProductResponse>
    }

//    // READ: ดึงสินค้าตาม ID
//    public Optional<Product> getProductById(Long id) {
//        return productRepository.findById(id);
//    }
    public Optional<ProductResponse> getProductResponseById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse);
    }

    // UPDATE: อัปเดตข้อมูลสินค้า
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        // 1. ค้นหาสินค้าเดิม
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 2. อัปเดตข้อมูล
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        existingProduct.setStockQuantity(request.stockQuantity());

        // 3. บันทึกและคืนค่า
        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct); // Response DTO
    }

    // DELETE: ลบสินค้า
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

}