package com.example.order_service.controller;

import com.example.order_service.DTO.UpdateOrderStatusRequest;
import com.example.order_service.service.OrderSellerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final OrderSellerService orderSellerService;

    public SellerOrderController(OrderSellerService orderSellerService) {
        this.orderSellerService = orderSellerService;
    }

    // 1 & 4. Get all order & filter
    // URL: GET
    // /api/admin/orders?shopId=1&status=PENDING&startDate=2026-05-01&page=0&size=10
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            var ordersPage = orderSellerService.getFilteredOrders(shopId, status, startDate, endDate, page, size);

            return ResponseEntity.ok(Map.of(
                    "message", "Berhasil mengambil data pesanan",
                    "data", ordersPage.getContent(), // Data list orders
                    "totalPages", ordersPage.getTotalPages(),
                    "totalItems", ordersPage.getTotalElements(),
                    "currentPage", ordersPage.getNumber()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Terjadi kesalahan saat mengambil data pesanan",
                    "error", e.getMessage()));
        }
    }

    // 2. Process order (Update status & input resi)
    // URL: PATCH /api/admin/orders/15/process
    @PatchMapping("/{orderId}/process")
    public ResponseEntity<?> processOrder(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            var updatedOrder = orderSellerService.processOrder(orderId, request);

            return ResponseEntity.ok(Map.of(
                    "message", "Status pesanan berhasil diperbarui",
                    "data", updatedOrder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Gagal memproses pesanan", "error", e.getMessage()));
        }
    }

    // 3. Get data statistics for dashboard
    // URL: GET /api/admin/orders/statistics?shopId=1
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics(
            @RequestParam(required = false) Long shopId) {
        try {
            var statistics = orderSellerService.getDashboardStatistics(shopId);

            return ResponseEntity.ok(Map.of(
                    "message", "Berhasil mengambil statistik dashboard",
                    "data", statistics));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Gagal mengambil data statistik dashboard"));
        }
    }
}