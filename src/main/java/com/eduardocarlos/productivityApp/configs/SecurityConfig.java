package com.eduardocarlos.productivityApp.configs;

import com.eduardocarlos.productivityApp.security.JwtAuthenticationFilter;
import com.eduardocarlos.productivityApp.security.JwtAuthorizationFilter;
import com.eduardocarlos.productivityApp.security.JwtTokenService;
import com.eduardocarlos.productivityApp.services.UserDetailsImplService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsImplService userDetailsImplService;
    private final JwtTokenService jwtTokenService;

    public SecurityConfig(UserDetailsImplService userDetailsImplService, JwtTokenService jwtTokenService) {
        this.userDetailsImplService = userDetailsImplService;
        this.jwtTokenService = jwtTokenService;
    }

    public static final String[] ENDPOINTS_WITHOUT_AUTHENTICATION_REQUIRED = {
            "/login", // login
            "/user", // creation
            "/"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, ENDPOINTS_WITHOUT_AUTHENTICATION_REQUIRED).permitAll()
                        .anyRequest().authenticated()).authenticationManager(authenticationManager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager, this.jwtTokenService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthorizationFilter(this.jwtTokenService, this.userDetailsImplService, authenticationManager), BasicAuthenticationFilter.class);

        http.sessionManagement((session) ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(this.userDetailsImplService).passwordEncoder(encoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
