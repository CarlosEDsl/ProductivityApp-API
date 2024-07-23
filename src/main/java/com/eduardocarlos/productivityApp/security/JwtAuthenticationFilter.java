package com.eduardocarlos.productivityApp.security;

import com.eduardocarlos.productivityApp.exceptions.GlobalExceptionHandler;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthenticatedUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try{
            User user = new ObjectMapper().readValue(req.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), user.getPassword(), new ArrayList<>()
            );

            Authentication auth = this.authenticationManager.authenticate(token);
            return auth;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain, Authentication authentication)
            throws IOException, ServletException {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String token = this.jwtTokenService.generateToken(user);
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
    }

}
