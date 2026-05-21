package com.example.order_service.controller;

import com.example.order_service.DTO.MidtransNotificationDto;
import com.example.order_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Constructor Injection
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Endpoint Webhook: POST http://localhost:8080/api/payments/midtrans-webhook
     * Digunakan oleh Server Midtrans untuk mengirimkan status pembayaran terbaru
     * secara realtime.
     */
    @PostMapping("/midtrans-webhook")
    public ResponseEntity<String> handleMidtransNotification(@RequestBody MidtransNotificationDto notification) {
        try {
            // Memproses notifikasi masuk
            paymentService.processMidtransNotification(notification);

            // Midtrans membutuhkan respon HTTP Status 200 dengan body teks "OK" atau kosong
            // sebagai tanda bahwa Webhook berhasil diterima dengan baik oleh backend kita.
            return ResponseEntity.ok("OK");

        } catch (IllegalArgumentException e) {
            // Jika data ID Transaksi tidak ditemukan di DB kita
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (java.security.SignatureException e) {
            // Jika Signature Key tidak cocok (Indikasi manipulasi data)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Menangkap error umum lainnya
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error mengolah data: " + e.getMessage());
        }
    }
}