package com.example.order_service.DTO;

public record UpdateOrderStatusRequest(
        String status, // PROCESSING, SHIPPED, DELIVERED
        String trackingNumber) {
}