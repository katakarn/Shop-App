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

    public String generateToken(UserDetails user) {
        return generateToken(Map.of(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails user) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + props.getExpiration());

        // สำหรับ 0.11.5 ใช้ setClaims/setSubject/setIssuedAt/setExpiration
        return Jwts.builder()
                .setClaims(new HashMap<>(extraClaims))      // หรือ .addClaims(...) ก็ได้
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ต้องระบุ algo
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
        byte[] keyBytes = Decoders.BASE64.decode(props.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
