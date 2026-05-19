package com.example.order_service.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime; // atau pakai java.util.Date sesuai tipe di Entity Order-mu

import com.example.order_service.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResponse {
    private Long id;
    private String transactionId;
    private String shopName;
    private BigDecimal finalTotalPrice; 
    private String orderStatus;
    private LocalDateTime orderDate;
}