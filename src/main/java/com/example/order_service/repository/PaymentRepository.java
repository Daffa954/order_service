package com.example.order_service.repository;

import com.example.order_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Mencari data pembayaran berdasarkan ID Transaksi unik.
     * Menggunakan Optional untuk penanganan Null Pointer Exception yang lebih aman
     * saat data tidak ditemukan di database.
     */
    Optional<Payment> findByTransactionId(String transactionId);
}