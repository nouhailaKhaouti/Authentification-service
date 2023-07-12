package com.codetech.authserver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${spring.security.oauth2.registration.keycloak.client-secret}")
    private String SECRET_KEY ;
    private static final LocalDateTime EXPIRATION_TIME = LocalDateTime.now().plus(1000000, ChronoUnit.SECONDS);

    public boolean isTokenExpired(String token) {
        Long expirationTimestamp = extractClaims(token).get("expirationDate", Long.class);
        Date expirationDate = new Date(expirationTimestamp);
        return expirationDate.before(new Date());
    }
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean verifyToken(String keycloakToken, String customToken) {
        try {
            return customToken.equals(keycloakToken);
        } catch (JwtException e) {
            return false;
        }
    }
    public String getEmailFromToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    public String generateToken(String email) {
        LocalDateTime expirationDateTime = EXPIRATION_TIME;
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("expirationDate", expirationDate);

        return  Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

}