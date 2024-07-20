package com.eduardocarlos.productivityApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Service
public class JwtTokenService {

    @Value("{jwt.secret}")
    private String secretkey;
    @Value("{jwt.expiration}")
    private String expiration;

    public String generateToken(UserDetailsImpl user){

        SecretKey key = Keys.hmacShaKeyFor(this.secretkey.getBytes());

        return Jwts.builder()
                .content(user.getUsername())
                .signWith(key)
                .expiration(new Date(System.currentTimeMillis() + expiration))
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

        SecretKey key = Keys.hmacShaKeyFor(this.secretkey.getBytes());

        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        try{
            return claims;
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }



    
}
