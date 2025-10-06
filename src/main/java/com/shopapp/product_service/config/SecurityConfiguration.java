package com.shopapp.product_service.config;

import com.shopapp.product_service.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.shopapp.product_service.model.Role.ADMIN;
import static com.shopapp.product_service.model.Role.USER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                // 1. Endpoint สำหรับ Public (ทุกคนเข้าได้)
                .requestMatchers("/api/v1/auth/**").permitAll() // สำหรับ Register/Login
                .requestMatchers(GET, "/api/v1/products/**").permitAll() // ดูสินค้าทุกคนดูได้

                // 2. Endpoint สำหรับ ADMIN เท่านั้น
                .requestMatchers(POST, "/api/v1/products/**").hasAnyAuthority(ADMIN.name()) // สร้างสินค้า
                .requestMatchers(PUT, "/api/v1/products/**").hasAnyAuthority(ADMIN.name())  // แก้ไขสินค้า
                .requestMatchers(DELETE, "/api/v1/products/**").hasAnyAuthority(ADMIN.name()) // ลบสินค้า
                .requestMatchers("/api/v1/orders/admin/**").hasAnyAuthority(ADMIN.name()) // ดูคำสั่งซื้อทั้งหมด

                // 3. Endpoint สำหรับ USER (Customer) เท่านั้น
                .requestMatchers("/api/v1/cart/**").hasAnyAuthority(USER.name()) // ตะกร้าสินค้า
                .requestMatchers("/api/v1/orders/**").hasAnyAuthority(USER.name()) // สั่งซื้อสินค้า, ดูประวัติการสั่งซื้อ

                // 4. Endpoint อื่นๆ ที่ต้อง Login
                .anyRequest().authenticated()).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authenticationProvider(authenticationProvider).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}