package com.Address.demo.security;

//package com.Address.demo.security.JwtHelper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtHelper {

    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 hours

    private final Key key = Keys.hmacShaKeyFor(
            "mysecretkeymysecretkeymysecretkey123".getBytes()
    );

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
