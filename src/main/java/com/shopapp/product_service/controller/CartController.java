package com.shopapp.product_service.controller;

import com.shopapp.product_service.dto.CartRequest;
import com.shopapp.product_service.dto.CartResponse;
import com.shopapp.product_service.model.Cart;
import com.shopapp.product_service.model.user.User;
import com.shopapp.product_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET /api/v1/cart - ดึงตะกร้าสินค้าของฉัน
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal User user ดึง User ที่ล็อกอินแล้วมาให้โดยอัตโนมัติ
        return ResponseEntity.ok(cartService.getOrCreateCartDTO(user));
    }

    // POST /api/v1/cart - เพิ่มสินค้าในตะกร้า
    @PostMapping
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal User user,
            @RequestBody CartRequest request) {

        CartResponse updatedCart = cartService.addItemToCart(user, request.productId(), request.quantity());
        return ResponseEntity.ok(updatedCart);
    }

    // DELETE /api/v1/cart/{productId} - ลบรายการสินค้าออกจากตะกร้า
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {

        CartResponse updatedCart = cartService.removeItemFromCart(user, productId);
        return ResponseEntity.ok(updatedCart);
    }
}