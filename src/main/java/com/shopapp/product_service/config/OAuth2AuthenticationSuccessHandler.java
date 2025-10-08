package com.shopapp.product_service.config;

import com.shopapp.product_service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService; // เอาไว้ “ออก JWT”

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1) ดึงข้อมูลผู้ใช้จาก OAuth2User แบบปลอดภัย (บาง provider อาจไม่มีบาง field)
        String email = null;
        String name  = null;
        try {
            var oauth2 = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            email = oauth2.<String>getAttribute("email");
            name  = oauth2.<String>getAttribute("name");
        } catch (ClassCastException ignored) { /* กันไว้เฉย ๆ */ }

        if (email == null) {
            // ถ้าไม่มี email จริง ๆ ให้ตั้ง subject พื้นฐาน (คุณจะเลือกโยน error ก็ได้)
            email = "oauth2_user";
        }

        // 2) ออก JWT (เดี๋ยวเราจะนิยามใน JwtService)
        String jwt = jwtService.generateToken(
                java.util.Map.of("name", name), // claims อื่น ๆ ใส่ได้ตามต้องการ
                email                          // subject (unique)
        );

        // 3) ส่ง JWT กลับเป็นคุกกี้ HttpOnly (เหมาะกับเว็บ/SPA)
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", jwt)
                .httpOnly(true)
                .secure(false)        // โปรดักชันบน HTTPS ให้ true
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // 4) เคลียร์ session เพื่อคงความเป็น stateless (เราใช้ JWT ต่อไป)
        var session = request.getSession(false);
        if (session != null) session.invalidate();

        // 5) redirect ไปหน้า success ที่ “ไม่อ่าน principal”
        response.sendRedirect("/oauth2/loginSuccess");
    }
}
