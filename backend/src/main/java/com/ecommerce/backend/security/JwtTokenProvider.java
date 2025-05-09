package com.ecommerce.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecretKey = Keys.hmacShaKeyFor("çokgüçlüvesırlıbiranahtar1234567890".getBytes());
    private final long jwtExpirationMs = 86400000; // 1 gün

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    // ✅ BU METODU EKLE
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
}
