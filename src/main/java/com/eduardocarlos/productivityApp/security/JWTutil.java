package com.eduardocarlos.productivityApp.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTutil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private String jwtExpiration;

    public SecretKey getKeyBySecret(){
        return Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }

    public String generateToken(String login) {
        SecretKey key = this.getKeyBySecret();

        return Jwts.builder().content(login)
                .signWith(key).expiration(new Date(System.currentTimeMillis() + this.jwtExpiration))
                .compact();
    }

    public boolean isValidToken(String token){
        Claims claims = getClaims(token);

        if(Objects.nonNull(claims)) {
            String login = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            //return true if token is valid with this verifications like if the expiration is ok
            return Objects.nonNull(login) && Objects.nonNull(expirationDate) && now.before(expirationDate);
        }
        return false;
    }

    private Claims getClaims(String token) {
        SecretKey key = getKeyBySecret();

        try{
            return Jwts.parser().verifyWith(key)
                    .build().parseSignedClaims(token)
                    .getPayload();
        } catch(Exception e) {
            //Will make an exception handler
            return null;
        }
    }


}
