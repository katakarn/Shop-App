package com.shopapp.product_service.service;

import com.shopapp.product_service.dto.CartItemDto;
import com.shopapp.product_service.dto.CartResponse;
import com.shopapp.product_service.model.*;
import com.shopapp.product_service.model.user.User;
import com.shopapp.product_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private CartResponse mapToCartResponse(Cart cart) {
        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        int totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        var itemDto = cart.getItems().stream()
                .map(item -> new CartItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getPrice() * item.getQuantity()
                ))
                .toList();
        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                totalPrice,
                totalItems,
                itemDto
        );
    }


    // 1. ดึงตะกร้าสินค้าของผู้ใช้ (ถ้าไม่มี ให้สร้างใหม่)
    public Cart getOrCreateCart(User user) {
        // ลองหาตะกร้าที่มีอยู่แล้ว
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    // ถ้าไม่มี ให้สร้าง Cart ใหม่
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // GET /cart → คืน DTO
    @Transactional
    public CartResponse getOrCreateCartDTO(User user) {
        // ถ้ามี method fetch-join ใช้แทน findByUser เพื่อลดปัญหา LAZY:
        // Cart cart = cartRepository.findByUserFetchItemsAndProduct(user).orElseGet(() -> getOrCreateCart(user));
        Cart cart = getOrCreateCart(user);
        return mapToCartResponse(cart);
    }


    // 2. เพิ่ม/อัปเดต สินค้าในตะกร้า
    @Transactional
    public CartResponse addItemToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ค้นหารายการสินค้าเดิมในตะกร้า
        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem(null, cart, product, quantity);
                            cartItemRepository.save(newItem);
                            cart.getItems().add(newItem);
                        });
        cartRepository.save(cart);

        // โหลด Cart อีกครั้งพร้อมรายการ (เพื่อความง่ายในการทดสอบ)
        // return cartRepository.findById(cart.getId()).get();
        //        .orElseThrow(() -> new RuntimeException("Cart not found: " + cart.getId()));
        return mapToCartResponse(cart);
    }

    // 3. ลบสินค้าออกจากตะกร้า
    @Transactional
    public CartResponse removeItemFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);

        // ค้นหารายการสินค้าที่จะลบ
        CartItem itemToDelete = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartItemRepository.delete(itemToDelete);

        // โหลด Cart อีกครั้ง
        // return cartRepository.findById(cart.getId()).get();
        //        return cartRepository.findById(cart.getId())
        //                .orElseThrow(() -> new RuntimeException("Cart not found: " + cart.getId()));
        return mapToCartResponse(cart);
    }
}