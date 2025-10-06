package com.shopapp.product_service.repository;

import com.shopapp.product_service.model.Cart;
import com.shopapp.product_service.model.CartItem;
import com.shopapp.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ค้นหารายการสินค้าในตะกร้าจาก Cart และ Product นั้นๆ
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // ค้นหารายการสินค้าตาม Product ID และ Cart ID
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}