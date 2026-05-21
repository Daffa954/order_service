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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${midtrans.server-key}")
    private String serverKey;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void processMidtransNotification(MidtransNotificationDto notification) throws Exception {
        
        // 1. Validasi Keamanan (Signature Key Verification)
        String signatureData = notification.getOrder_id() + notification.getStatus_code() + 
                               notification.getGross_amount() + serverKey.trim();
        
        String mySignature = hashSha512(signatureData);
        
        if (!mySignature.equalsIgnoreCase(notification.getSignature_key())) {
            throw new Exception("Invalid Signature Key! Request ditolak demi keamanan.");
        }

        // 2. Cari data Payment dan Order di Database
        Payment payment = paymentRepository.findByTransactionId(notification.getOrder_id())
                .orElseThrow(() -> new Exception("Transaction ID tidak ditemukan"));
                
        List<Order> orders = orderRepository.findByTransactionId(notification.getOrder_id());

        // 3. Update Status berdasarkan respons Midtrans
        String transactionStatus = notification.getTransaction_status();
        
        if (transactionStatus.equals("settlement") || transactionStatus.equals("capture")) {
            // Jika Lunas
            payment.setPaymentStatus("PAID");
            orders.forEach(order -> order.setOrderStatus(OrderStatus.PROCESSED)); // Atau enum PAID milikmu
            
        } else if (transactionStatus.equals("cancel") || transactionStatus.equals("deny") || transactionStatus.equals("expire")) {
            // Jika Gagal/Batal
            payment.setPaymentStatus("FAILED");
            orders.forEach(order -> order.setOrderStatus(OrderStatus.CANCELLED));
            
        } else if (transactionStatus.equals("pending")) {
            // Masih menunggu pembayaran (Tidak ada perubahan)
            payment.setPaymentStatus("PENDING");
        }

        // 4. Simpan perubahan ke Database
        paymentRepository.save(payment);
        orderRepository.saveAll(orders);
    }

    // Fungsi helper untuk melakukan Enkripsi SHA-512 sesuai standar Midtrans
    private String hashSha512(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal menghitung hash", e);
        }
    }
}