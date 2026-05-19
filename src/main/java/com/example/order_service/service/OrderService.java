package com.example.order_service.service;

import com.example.order_service.DTO.OrderListResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.Payment;
import com.example.order_service.repository.OrderRepository;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Mengambil semua data order
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // getOrderBasedOnUserId
    public List<OrderListResponse> getOrderListBasedOnUserId(Long userId) {
        List<Order> orders = orderRepository.findByCustomerId(userId);

        // Proses mengubah Entity Order menjadi DTO OrderListResponse
        return orders.stream().map(order -> new OrderListResponse(
                order.getId(),
                order.getTransactionId(),
                order.getShopName(),
                order.getTotalAmount(),
                order.getOrderStatus().toString(),
                order.getOrderDate())).collect(Collectors.toList());

    }

}