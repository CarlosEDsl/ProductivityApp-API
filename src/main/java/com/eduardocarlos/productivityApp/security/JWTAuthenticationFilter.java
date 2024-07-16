package com.eduardocarlos.productivityApp.security;

import com.eduardocarlos.productivityApp.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTutils jwTutils;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTutils jwTutils) {
        this.authenticationManager = authenticationManager;
        this.jwTutils = jwTutils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            User userCred = new ObjectMapper().readValue(req.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCred.getEmail(), userCred.getPassword(), new ArrayList<>());

            Authentication auth = this.authenticationManager.authenticate(authToken);
            return auth;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain filter, Authentication auth){
    }
}
