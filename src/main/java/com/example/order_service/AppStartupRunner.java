package com.example.order_service;

import org.springframework.stereotype.Component;

import org.springframework.boot.CommandLineRunner;

import com.example.order_service.service.LocationSeederService;

@Component
public class AppStartupRunner implements CommandLineRunner {

    private final LocationSeederService seederService;

    public AppStartupRunner(LocationSeederService seederService) {
        this.seederService = seederService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ini akan berjalan otomatis setiap kali aplikasi start
        seederService.seedLocations();
    }
}