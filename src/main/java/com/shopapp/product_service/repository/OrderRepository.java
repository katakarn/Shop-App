package com.shopapp.product_service.repository;

import com.shopapp.product_service.model.Order;
import com.shopapp.product_service.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user); // สำหรับดึงประวัติการสั่งซื้อของลูกค้า
}
