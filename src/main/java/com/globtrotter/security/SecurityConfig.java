package com.globtrotter.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/public/**",
                                "/products/**"
                        ).permitAll()
                        .requestMatchers("/products/admin/**")
                        .hasAnyRole("ADMIN", "VENDOR")  // Ensure both "ROLE_ADMIN" and "ROLE_VENDOR" can access
                        .requestMatchers("/cart/**")
                        .hasRole("CUSTOMER")  // Ensure access to cart is restricted to "ROLE_CUSTOMER"
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")  // Ensure only "ROLE_ADMIN" can access
                        .requestMatchers("/vendor/**")
                        .hasRole("VENDOR")  // Ensure only "ROLE_VENDOR" can access
                        .requestMatchers("/customer/**")
                        .hasRole("CUSTOMER")  // Ensure only "ROLE_CUSTOMER" can access
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // Basic Auth enabled

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
