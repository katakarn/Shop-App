package com.shopapp.product_service.repository;

import com.shopapp.product_service.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ค้นหา User จาก Email (ซึ่งใช้เป็น Username)
    Optional<User> findByEmail(String email);
}