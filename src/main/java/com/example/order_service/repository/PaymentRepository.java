package com.example.order_service.repository;

import com.example.order_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Kustom Query: Sangat berguna nanti saat Midtrans mengirim Webhook /
    // Notifikasi.
    // Spring Boot akan otomatis mengerti maksud fungsi ini dari namanya!
    Payment findByMidtransTransactionId(String midtransTransactionId);
}