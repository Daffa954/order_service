package com.example.order_service.controller;

import com.example.order_service.DTO.MidtransNotificationDto;
import com.example.order_service.entity.Payment;
import com.example.order_service.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Constructor Injection
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Endpoint Webhook: POST http://localhost:8080/orderservice/payments/midtrans-webhook
     * Digunakan oleh Server Midtrans untuk mengirimkan status pembayaran terbaru
     * secara realtime.
     */
    @PostMapping("/midtrans-webhook")
    public ResponseEntity<Object> handleMidtransNotification(@RequestBody MidtransNotificationDto notification) {
        try {
            // Memproses notifikasi masuk dan menangkap data Payment yang terupdate
            Payment updatedPayment = paymentService.processMidtransNotification(notification);

            // Membuat format JSON yang rapi (mirip APIResponse kamu)
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Webhook Midtrans berhasil diproses");
            response.put("data", updatedPayment); // Memasukkan seluruh data payment ke sini

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", 404, "message", "Error: " + e.getMessage()));
            
        } catch (java.security.SignatureException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", 403, "message", "Error: " + e.getMessage()));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", 500, "message", "Error mengolah data: " + e.getMessage()));
        }
    }
}