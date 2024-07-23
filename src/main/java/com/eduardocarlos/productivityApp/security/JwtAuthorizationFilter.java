package com.eduardocarlos.productivityApp.security;

import com.eduardocarlos.productivityApp.services.UserDetailsImplService;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthenticatedUserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtTokenService jwtTokenService;
    private UserDetailsImplService userDetailsService;

    public JwtAuthorizationFilter(JwtTokenService jwtTokenService, UserDetailsImplService userDetailsImplService, AuthenticationManager authenticationManager){
        super(authenticationManager);
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsImplService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {

        String authToken = req.getHeader("Authorization");

        if(Objects.nonNull(authToken) && authToken.startsWith("Bearer")){
            String token = authToken.substring(7);
            UsernamePasswordAuthenticationToken auth = this.getAuthentication(token);
            if(Objects.nonNull(auth)){
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token){
        if(this.jwtTokenService.verifyToken(token)){
            String login = jwtTokenService.getEmail(token);

            UserDetails user = this.userDetailsService.loadUserByUsername(login);
            return new UsernamePasswordAuthenticationToken(user,
                    null,
                    user.getAuthorities());
        }
        throw new UnauthenticatedUserException();
    }

}
