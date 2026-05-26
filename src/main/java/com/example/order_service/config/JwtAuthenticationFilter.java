package com.example.order_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim(); 
            
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                
                System.out.println("✅ [JWT] Tanda tangan (Signature) Valid!");

                // 1. Ekstrak User ID (Handle tipe data String maupun Angka/Long dari Node.js)
                String userId = null;
                if (!decodedJWT.getClaim("userId").isNull()) {
                    userId = decodedJWT.getClaim("userId").asString();
                    if (userId == null) {
                        userId = String.valueOf(decodedJWT.getClaim("userId").asLong());
                    }
                }

                // 2. Ekstrak Role
                String role = null;
                if (!decodedJWT.getClaim("role").isNull()) {
                    role = decodedJWT.getClaim("role").asString();
                }
                
                System.out.println("✅ [JWT] Isi Payload -> UserID: " + userId + ", Role: " + role);

                // 3. Validasi Kehadiran Data
                if (userId != null && role != null) {
                    String springSecurityRole = "ROLE_" + role.toUpperCase();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, List.of(new SimpleGrantedAuthority(springSecurityRole))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ [JWT] Akses Diberikan untuk role: " + springSecurityRole);
                } else {
                    System.err.println("❌ [JWT] Ditolak: Payload 'userId' atau 'role' KOSONG di dalam token!");
                }

            } catch (Exception e) {
                System.err.println("❌ [JWT] Token Invalid/Expired: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}