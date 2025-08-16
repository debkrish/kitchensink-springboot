package com.example.kitchensink.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Getter
    private final String secret = "my-very-strong-secret-key-of-at-least-256-bits-length";
    private final long expirationMs = 3600000; // 1 hour

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** Generate JWT with subject and optional extra claims */
    public String generateToken(String subject, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        var builder = Jwts.builder()
                .claim("sub", subject)
                .claim("iat", now / 1000)
                .claim("exp", (now + expirationMs) / 1000);

        extraClaims.forEach(builder::claim);

        return builder
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                return null; // token expired
            }

            return claims.getSubject();
        } catch (Exception e) {
            return null; // invalid token
        }
    }

}