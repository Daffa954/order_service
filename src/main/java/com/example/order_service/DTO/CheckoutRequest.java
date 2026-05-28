package com.example.order_service.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CheckoutRequest {
    private Long customerId;
    private String customerName;
    
    // --- Data Alamat Lengkap ---
    private String shippingAddress;
    private String shippingProvince;
    private String shippingCity;
    private String shippingDistrict;
    
    // ID untuk referensi Komerce/RajaOngkir
    private Long destinationCityId; 

    // --- List Pesanan per Toko ---
    // Variabel "items" dan kurir global dihapus, diganti menjadi array per toko
    private List<ShopOrderDto> shopOrders; 

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


}