package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. RUTE PUBLIK
                        .requestMatchers("/api/payments/midtrans-webhook").permitAll() 
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() 
                        .requestMatchers("/api/rajaongkir/**").permitAll() // Sesuai dengan RajaOngkirController

                        // 2. RUTE KHUSUS SELLER
                        // Wajib pakai token milik Seller
                        .requestMatchers("/api/seller/orders/**").hasRole("SELLER")

                        // 3. RUTE CUSTOMER (PEMBELI)
                        // Wajib pakai token (bisa token USER atau SELLER, karena seller juga bisa belanja)
                        .requestMatchers("/order/customer/**").authenticated() 
                        .requestMatchers("/api/orders/**").authenticated()

                        // 4. ATURAN DEFAULT
                        .anyRequest().authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}