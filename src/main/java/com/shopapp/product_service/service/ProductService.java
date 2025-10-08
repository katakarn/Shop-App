package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.ProductRequest;
import com.shopapp.product_service.dto.ProductResponse;
import com.shopapp.product_service.exception.ResourceNotFoundException;
import com.shopapp.product_service.model.Product;
import com.shopapp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.spi.JpaTargetAndSourceDescriptor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

//    // 1. READ: ดึงสินค้าทั้งหมด: แคชผลลัพธ์ของสินค้าทั้งหมด
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
    @Cacheable(value = "product", key = "'all'") // ชื่อ Cache คือ "products", Key คือ "all"
    public List<ProductResponse> getAllProduct() {
        log.info("✅✅✅ Fetching all products from database...");
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList(); // แปลง List<Product> เป็น List<ProductResponse>
//                .toArray(ProductResponse[]::new);
    }

//    // 2. READ: ดึงสินค้าตาม ID: แคชผลลัพธ์ของสินค้าแต่ละชิ้น
//    public Optional<Product> getProductById(Long id) {
//        return productRepository.findById(id);
//    }
    @Cacheable(value = "product", key = "#id") // ชื่อ Cache คือ "product", Key คือ Product ID
    public ProductResponse getProductResponseById(Long id) {
        var existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(existing);
    }

//    // 3. CREATE: บันทึกสินค้าใหม่
//    public Product createProduct(Product product) {
//        // อาจเพิ่ม Business Logic เช่น การตรวจสอบ (Validation) ก่อนบันทึก
//        return productRepository.save(product);
//    }
    // 4. CREATE: สร้างสินค้าใหม่: ต้องล้าง Cache ทั้งหมด
    @CacheEvict(value = {"products", "product"}, allEntries = true) // ล้าง Cache 'products' และ 'product' ทั้งหมด
    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapToProduct(request);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }


    // 5. UPDATE: อัปเดตข้อมูลสินค้า: ต้องล้าง Cache ทั้งหมด
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        // 1. ค้นหาสินค้าเดิม
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // 2. อัปเดตข้อมูล
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        existingProduct.setStockQuantity(request.stockQuantity());

        // 3. บันทึกและคืนค่า
        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct); // Response DTO
    }

    // 6. DELETE: ลบสินค้า: ต้องล้าง Cache ทั้งหมด
    @CacheEvict(value = {"products","product"}, allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

}