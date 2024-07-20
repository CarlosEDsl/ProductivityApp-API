package com.eduardocarlos.productivityApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetailsImpl user){

        SecretKey key = Keys.hmacShaKeyFor(this.secretKey.getBytes());

        return Jwts.builder()
                .setSubject(user.getUsername())  // Set the subject to the username
                .signWith(key, SignatureAlgorithm.HS256)  // Sign with the key
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // Set expiration
                .compact();
    }

    public boolean verifyToken(String token) {

        Claims claims = getClaims(token);
        if(Objects.nonNull(claims)){
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            return Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate);

        }
        return false;

    }

    public String getEmail(String token){
        Claims claims = getClaims(token);
        if(Objects.nonNull(claims)){
            return claims.getSubject();
        }
        return null;
    }

    private Claims getClaims(String token){

        SecretKey key = Keys.hmacShaKeyFor(this.secretKey.getBytes());

        try{
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }




}
