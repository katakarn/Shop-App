package com.shopapp.product_service.auth;

import com.shopapp.product_service.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;

//    public JwtService(JwtProperties props) {
//        this.props = props;
//    }

//    @Value("${app.jwt.secret}")         // ควรเป็น Base64-encoded string
//    private String secret;
//
//    @Value("${app.jwt.expiration-ms:86400000}") // default 1 วัน
//    private long expirationMs;

    // ====== Public APIs ======

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = parseAllClaims(token);
        return resolver.apply(claims);
    }

    public String generateToken(String subject) {
        return generateToken(Map.of(), subject);
    }

    // ใช้กับ OAuth2: ใส่ชื่อ/claims อื่น ๆ ได้
    public String generateToken(Map<String, Object> extraClaims, String subject) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + props.getExpiration());

        Map<String, Object> claims = new HashMap<>(extraClaims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    // ====== Internal ======

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        String secret = props.getSecretKey();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret-key is missing. Set app.jwt.secret-key (Base64).");
        }
        try {
            byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // ตกมาใช้ raw bytes (ไม่แนะนำในโปรดักชัน แต่ช่วยกันพังตอน dev)
            return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
