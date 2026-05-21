
package com.example.order_service.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CostRequestDto {
    private String origin;          // ID Kecamatan/Kota Asal
    private String destination;     // ID Kecamatan/Kota Tujuan
    private int weight;             // Berat dalam gram
    private String couriers;        // "jne:sicepat:jnt" dsb

    private List<ShopOrderDto> shopOrders;
}