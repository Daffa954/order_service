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
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/customer") // URL jelas khusus customer
public class CustomerOrderController {
    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint: GET http://localhost:8080/orderservice/customer/{customerId}
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 2. Ambil ID User dari Principal
            String customerId = (String) authentication.getPrincipal();

            // 3. Timpa ID dari frontend dengan ID asli dari token JWT
            request.setCustomerId(Long.parseLong(customerId));
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

    // Endpoint: GET
    // http://localhost:8080/orderservice/customer/order/{transactionId}
    @GetMapping("/order/{transactionId}")
    public ResponseEntity<APIResponse<Order>> getDetailOrder(@PathVariable String transactionId) {
        try {
            // 1. Ambil ID User dari token JWT (Keamanan)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerIdStr = (String) authentication.getPrincipal();
            Long customerId = Long.parseLong(customerIdStr);

            // 2. Panggil Service untuk mencari detail transaksi
            Order order = orderService.getOrderDetail(transactionId, customerId);

            // 3. Jika data tidak ada atau bukan milik customer ini
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(
                        404,
                        "Transaksi dengan ID " + transactionId + " tidak ditemukan atau akses ditolak.",
                        null));
            }

            // 4. Jika sukses
            return ResponseEntity.ok(new APIResponse<>(
                    200,
                    "Berhasil mengambil detail order",
                    order));

        } catch (IllegalArgumentException e) {
            // Menangkap error khusus keamanan (Unauthorized)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(
                    403,
                    e.getMessage(),
                    null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse<>(
                    400,
                    "Terjadi kesalahan sistem: " + e.getMessage(),
                    null));
        }
    }
    // Create order not from cart

    // Create order from cart

    // Create order from cart if user want to checkout from different store

    // pay

    // Finish order

}