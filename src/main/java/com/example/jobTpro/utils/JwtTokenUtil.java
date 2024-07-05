package com.example.jobTpro.utils;

import com.example.jobTpro.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtTokenUtil {
    private static final long JWT_TOKEN_VALIDITY =  24000 * 60 * 60 * 1000; // 24 hours
    private String secret = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b"; // Replace with your actual secret key

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public int getUserIdFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .get("userId", Integer.class);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Ignore the expiration check
        if (claims.getExpiration().before(new Date())) {
            // Token has expired but we're ignoring the expiration check
            System.out.println("Token has expired but ignoring expiration check");
        }

        return claims.get("userId", Integer.class);

    }

    public boolean validateToken(String token, User user) {
        final String userIdFromToken = String.valueOf(getUserIdFromToken(token));
        return (userIdFromToken.equals(String.valueOf(user.getId())) && !isTokenExpired(token));
    }



    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }


}
