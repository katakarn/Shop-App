package com.shopapp.product_service.repository;

import com.shopapp.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// Product คือ Entity, Long คือชนิดของ Primary Key
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA จะสร้าง Method ในการค้นหา (Find) อัตโนมัติ
    // ถ้าเราตั้งชื่อ method ตาม convention
    // ตัวอย่าง:
    // List<Product> findByNameContaining(String name);
}