package com.example.order_service.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class APIKeyFilter extends OncePerRequestFilter {

    @Value("${service.api.key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Ambil password dari header (kita namakan "X-Service-Password")
        String requestApiKey = request.getHeader("X-Service-Password");

        // 2. Cek apakah passwordnya cocok
        if (requestApiKey == null || !requestApiKey.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": 401, \"message\": \"Akses ditolak: Service Password salah atau tidak ada!\"}");
            return; // Hentikan request sampai di sini
        }

        // 3. Jika cocok, persilakan masuk ke Controller
        filterChain.doFilter(request, response);
    }

    // PENTING: Tentukan rute mana saja yang HARUS pakai password ini
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Pengecualian! Midtrans tidak tahu password kita, jadi webhook JANGAN dikunci.
        if (path.startsWith("/api/payments/midtrans-webhook")) {
            return true; 
        }
        
        // Jika kamu ingin mengunci SEMUA rute (kecuali webhook), return false.
        // Tapi jika kamu hanya ingin mengunci rute khusus antar-service (misal /api/internal/**), 
        // kamu bisa atur logikanya di sini.
        return false; 
    }
}