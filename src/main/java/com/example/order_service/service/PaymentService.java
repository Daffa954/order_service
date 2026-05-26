package com.example.order_service.service;

import com.example.order_service.DTO.MidtransNotificationDto;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.Payment;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${midtrans.server-key}") // Ambil Server Key dari application.properties
    private String serverKey;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional // WAJIB ada agar sinkronisasi 2 tabel aman
    public Payment processMidtransNotification(MidtransNotificationDto notification) throws Exception {

        // 1. Validasi Keamanan (Signature Key)
        verifySignatureKey(notification);

        // 2. Ambil Data Transaksi dari Database
        // Catatan: Midtrans mengirim `order_id` yang sebenarnya adalah `transactionId`
        // di sistem kita
        String transactionId = notification.getOrderId();

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Data Payment tidak ditemukan"));

        // Ambil semua order (dari berbagai toko) yang menggunakan ID Transaksi ini
        List<Order> orders = orderRepository.findByTransactionId(transactionId);

        // 3. Cek Status dari Midtrans dan Update Data
        String transactionStatus = notification.getTransactionStatus();

        if (transactionStatus.equals("settlement") || transactionStatus.equals("capture")) {
            // === JIKA PEMBAYARAN LUNAS ===
            payment.setPaymentStatus("PAID");

            // 1. Simpan tipe pembayaran (misal: bca_va, qris, gopay)
            payment.setPaymentType(notification.getPaymentType());

            // 2. Simpan ID Resi asli dari Midtrans
            payment.setMidtransTransactionId(notification.getMidtransTransactionId());

            // 3. Simpan waktu pembayaran
            if (notification.getTransactionTime() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime payTime = LocalDateTime.parse(notification.getTransactionTime(), formatter);
                payment.setPaymentDate(payTime);
            }

            // Ubah semua order terkait menjadi PROCESSING
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.PROCESSING);
            }

        } else if (transactionStatus.equals("cancel") || transactionStatus.equals("deny")
                || transactionStatus.equals("expire")) {
            // === JIKA PEMBAYARAN GAGAL / KADALUARSA ===
            payment.setPaymentStatus("FAILED");

            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.CANCELLED);
            }

        } else if (transactionStatus.equals("pending")) {
            // === JIKA MASIH MENUNGGU PEMBAYARAN ===
            payment.setPaymentStatus("PENDING");
            // Status Order dibiarkan PENDING
        }

        // 4. Simpan Perubahan ke Database
        paymentRepository.save(payment);
        orderRepository.saveAll(orders);

        return payment;
    }

    // Fungsi Helper untuk mengecek Signature Key Midtrans
    private void verifySignatureKey(MidtransNotificationDto notification) throws Exception {
        String payloadData = notification.getOrderId() +
                notification.getStatusCode() +
                notification.getGrossAmount() +
                serverKey;

        // Hash menggunakan SHA-512
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashedBytes = md.digest(payloadData.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        String expectedSignature = sb.toString();

        if (!expectedSignature.equals(notification.getSignatureKey())) {
            throw new java.security.SignatureException("Invalid Signature Key!");
        }
    }
}