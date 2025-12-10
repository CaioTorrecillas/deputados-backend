package com.example.deputadosbackend.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWT {
    private final Key key;

    @Value("${jwt.access-expiration-minutes}")
    private long accessExpirationMinutes;

    @Value("${jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    public JWT(@Value("${jwt.secret}") String secret) {
        System.out.println("### JWT SECRET LIDO: " + secret);

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpirationMinutes * 60 * 1000);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Cria refresh token com jti (id) e retorna par: token + jti
    public TokenWithId createRefreshToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpirationDays * 24 * 60 * 60 * 1000);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .setSubject(subject)
                .setId(jti)          // jti salvo no DB
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new TokenWithId(token, jti, exp);
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
    public String validateTokenAndGetSubject(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token inválido: " + e.getMessage());
            return null;
        }
    }
    // DTO
    public static class TokenWithId {
        public final String token;
        public final String jti;
        public final Date expiration;
        public TokenWithId(String token, String jti, Date expiration) {
            this.token = token; this.jti = jti; this.expiration = expiration;
        }
    }
}
