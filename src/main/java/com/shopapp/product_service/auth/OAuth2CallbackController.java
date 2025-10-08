package com.shopapp.product_service.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OAuth2CallbackController {

    @GetMapping("/oauth2/loginSuccess")
    public ResponseEntity<?> success() {
        // ตอนนี้ JWT ถูกใส่ในคุกกี้ ACCESS_TOKEN แล้วจาก success handler
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/oauth2/loginFailure")
    public ResponseEntity<?> failure() {
        return ResponseEntity.status(401).body(Map.of("error", "oauth2 login failed"));
    }
}
