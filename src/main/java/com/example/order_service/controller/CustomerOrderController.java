package com.example.order_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.DTO.APIResponse;
import com.example.order_service.DTO.CheckoutRequest;
import com.example.order_service.DTO.OrderListResponse;
import com.example.order_service.service.OrderService;

@RestController
@RequestMapping("/order/customer") // URL jelas khusus customer
public class CustomerOrderController {
    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint: GET http://localhost:8080/order/customer/{customerId}
    @GetMapping("/{customerId}")
    public ResponseEntity<APIResponse<List<OrderListResponse>>> getOrdersByCustomerId(@PathVariable Long customerId) {
        // Memanggil fungsi DTO yang baru kita buat di service
        List<OrderListResponse> orders = orderService.getOrderListBasedOnUserId(customerId);

        if (orders.isEmpty()) {
            APIResponse<List<OrderListResponse>> response = new APIResponse<>(
                    404,
                    "Riwayat order untuk Customer ID " + customerId + " tidak ditemukan.",
                    orders);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Skenario jika ada data (Hanya mengirim field penting saja)
        APIResponse<List<OrderListResponse>> response = new APIResponse<>(
                200,
                "Berhasil mengambil data riwayat order milik customer.",
                orders);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<APIResponse<CheckoutResponse>> createOrder(@RequestBody CheckoutRequest request) {
        try {
            // Memanggil method createOrder yang sudah di-refactor menjadi clean code
            CheckoutResponse checkoutResponse = orderService.createOrder(request);

            // Mengembalikan status 200 OK dengan payload token Midtrans
            return ResponseEntity.ok(new APIResponse<>(
                    200,
                    "Order berhasil dibuat, silakan lanjut ke pembayaran",
                    checkoutResponse));
        } catch (Exception e) {
            // Menangkap error jika Midtrans mengalami kendala atau ada error database
            return ResponseEntity.badRequest().body(new APIResponse<>(
                    400,
                    "Gagal melakukan checkout: " + e.getMessage(),
                    null));
        }
    }

    // Create order not from cart

    // Create order from cart

    // Create order from cart if user want to checkout from different store

    // pay

    // Finish order

}