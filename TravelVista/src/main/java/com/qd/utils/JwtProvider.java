/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
/**
 *
 * @author ADMIN
 */
@Component
public class JwtProvider {
    private final String JWT_SECRET ="jVistaaTravelZuncuteihhruihnqZuncute196@oiqu93873i$$@61*9@&!(u3JAdjbJBDHABDIHYUWoejwiuiih@451";
    private final long JWT_EXPIRATION = 86400000L;
   
    private SecretKey getSigningKey() {
        byte[] keyBytes = this.JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
       
        public String generateToken(String username, String roleName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .subject(username)
                .claim("role", roleName)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) 
                .compact();
    }
        
        public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) 
                .compact();
    }
        
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            return false; 
        }
    }
}
