package com.shopapp.product_service.repository;

import com.shopapp.product_service.model.Cart;
import com.shopapp.product_service.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // ค้นหาตะกร้าตาม User (เนื่องจาก Cart ผูกกับ User 1:1)
    Optional<Cart> findByUser(User user);
}