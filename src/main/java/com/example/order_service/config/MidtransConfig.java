package com.example.order_service.config;

import com.midtrans.Midtrans;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidtransConfig {

    @Value("${midtrans.server-key}")
    private String serverKey;

    @Value("${midtrans.client-key}")
    private String clientKey;

    @Value("${midtrans.is-production}")
    private boolean isProduction;

    @PostConstruct
    public void init() {
        System.out.println("=== CEK MIDTRANS KEY ===");
        System.out.println("Server Key : " + serverKey);
        System.out.println("Is Production : " + isProduction);
        System.out.println("========================");
        // Menginisialisasi konfigurasi global Midtrans
        Midtrans.serverKey = this.serverKey;
        Midtrans.clientKey = this.clientKey;
        Midtrans.isProduction = this.isProduction;
    }
}