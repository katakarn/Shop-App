package com.shopapp.product_service.auth;

import com.shopapp.product_service.model.Role; // เพิ่ม import Role
import com.shopapp.product_service.model.user.User;
import com.shopapp.product_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Data Transfer Objects สำหรับรับ/ส่งข้อมูล
    record RegisterRequest(String email, String password, Role role) {}
    record AuthenticationRequest(String email, String password) {}
    record AuthenticationResponse(String token) {}

    // 1. REGISTER Endpoint: POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // ตรวจสอบว่า email ซ้ำหรือไม่
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        // กำหนด Role: ถ้าไม่ส่งมาให้เป็น USER, ถ้าส่งมาและเป็นค่าที่ถูกต้องก็ใช้ค่านั้น
        user.setRole(request.role() != null ? request.role() : Role.USER);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    // 2. AUTHENTICATE Endpoint: POST /api/v1/auth/authenticate
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        // ถ้า Authentication สำเร็จ
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found after successful auth"));

        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }
}