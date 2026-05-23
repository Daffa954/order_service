package com.example.order_service.controller;

import com.example.order_service.DTO.APIResponse;
import com.example.order_service.DTO.CheckoutRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint: GET http://localhost:8080/api/orders
    @GetMapping
    public ResponseEntity<APIResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            APIResponse<List<Order>> response = new APIResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "There is no order yet",
                    orders);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Pesan sukses jika data ditemukan
        APIResponse<List<Order>> response = new APIResponse<>(
                HttpStatus.OK.value(),
                "There is order",
                orders);
        return ResponseEntity.ok(response);
    }


    





    

}