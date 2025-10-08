package com.shopapp.product_service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 0) ข้ามเส้นทางสาธารณะ/เส้นทาง OAuth2
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) พยายามอ่านโทเคนจาก Authorization header ก่อน
        String jwt = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        // 2) ถ้าไม่มีใน header ลองอ่านจากคุกกี้ชื่อ ACCESS_TOKEN
        if (jwt == null) {
            jwt = readTokenFromCookie(request, "ACCESS_TOKEN");
        }

        // 3) ถ้ายังไม่มีโทเคน → ปล่อยผ่านเป็น anonymous
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) แกะ username จากโทเคน (กันโทเคนเสีย/หมดอายุ)
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // โทเคนไม่ถูกต้อง → ปล่อยผ่านเป็น anonymous
            filterChain.doFilter(request, response);
            return;
        }

        // 5) ตั้ง Authentication ถ้ายังไม่มีใน context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, user)) {
                var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    // ===== helpers =====
    private String readTokenFromCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
