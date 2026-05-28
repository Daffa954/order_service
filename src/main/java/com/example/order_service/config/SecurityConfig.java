package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final APIKeyFilter apiKeyFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, APIKeyFilter apiKeyFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. RUTE PUBLIK
                        .requestMatchers("/payments/midtrans-webhook").permitAll() 
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() 
                        .requestMatchers("/rajaongkir/**").permitAll() 

                        // 2. RUTE KHUSUS SELLER
                        .requestMatchers("/seller/**").hasRole("SELLER")

                        // 3. RUTE CUSTOMER (PEMBELI)
                        .requestMatchers("/customer/**").authenticated() 
                        .requestMatchers("/orders/**").authenticated()
.requestMatchers("/customer/order/**").permitAll()
                        // 4. ATURAN DEFAULT
                        .anyRequest().authenticated()
                )

                // =======================================================
                // TAMBAHKAN BLOK EXCEPTION HANDLING INI
                // =======================================================
                .exceptionHandling(exceptions -> exceptions
                        // Handler ketika token tidak ada / tidak valid (401 Unauthorized)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"status\": 401, \"message\": \"Akses ditolak: Anda belum login atau token tidak valid.\", \"data\": null}");
                        })
                        // Handler ketika role tidak sesuai (403 Forbidden)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"status\": 403, \"message\": \"Akses dilarang: Anda tidak memiliki izin (role) untuk rute ini.\", \"data\": null}");
                        })
                )
                // =======================================================

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}