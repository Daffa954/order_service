package com.example.order_service.service;

import com.example.order_service.DTO.UpdateOrderStatusRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderSellerService {

    private final OrderRepository orderRepository;

    public OrderSellerService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 1. FILTER & GET ALL ORDERS
    public Page<Order> getFilteredOrders(Long shopId, String status, String startDate, String endDate, int page, int size) {
        LocalDateTime start = (startDate != null && !startDate.isEmpty()) 
                ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        
        LocalDateTime end = (endDate != null && !endDate.isEmpty()) 
                ? LocalDateTime.parse(endDate + "T23:59:59") : null;
        
        return orderRepository.filterOrdersForAdmin(
                shopId, 
                status, 
                start, 
                end, 
                PageRequest.of(page, size)
        );
    }

    // 2. PROCESS ORDER (Update Status & Resi)
    public Order processOrder(Long orderId, UpdateOrderStatusRequest request) {
        // Cari order berdasarkan ID (PK tabel Order)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pesanan dengan ID " + orderId + " tidak ditemukan"));
        
        // Update Status
        if (request.status() != null && !request.status().isEmpty()) {
            order.setOrderStatus(OrderStatus.valueOf(request.status().toUpperCase()));
        }

        // Update Resi (Jika ada)
       
        
        return orderRepository.save(order);
    }

    // 3. GET DATA STATISTICS FOR DASHBOARD
    public Map<String, Object> getDashboardStatistics(Long shopId) {
        // Karena ini contoh, kita buat statistik sederhana. 
        // Jika butuh statistik per toko (shopId), query di repository harus disesuaikan.
        
        long totalPending = orderRepository.countOrdersByStatus("PENDING");
        long totalProcessing = orderRepository.countOrdersByStatus("PROCESSING");
        long totalShipped = orderRepository.countOrdersByStatus("SHIPPED");
        long totalDelivered = orderRepository.countOrdersByStatus("DELIVERED");

        Map<String, Object> stats = new HashMap<>();
        stats.put("pending_orders", totalPending);
        stats.put("processing_orders", totalProcessing);
        stats.put("shipped_orders", totalShipped);
        stats.put("delivered_orders", totalDelivered);
        stats.put("total_all_orders", (totalPending + totalProcessing + totalShipped + totalDelivered));

        return stats;
    }
}