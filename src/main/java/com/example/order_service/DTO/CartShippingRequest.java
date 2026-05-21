package com.example.order_service.DTO;

import java.util.List;

public class CartShippingRequest {
    private String originDistrictId;      // ID Kecamatan Toko Asal
    private String destinationDistrictId; // ID Kecamatan Pembeli
    private List<CheckoutItemDto> items;  // List barang yang mau dihitung beratnya

    // --- Getter dan Setter ---
    public String getOriginDistrictId() { 
        return originDistrictId; 
    }
    
    public void setOriginDistrictId(String originDistrictId) { 
        this.originDistrictId = originDistrictId; 
    }

    public String getDestinationDistrictId() { 
        return destinationDistrictId; 
    }
    
    public void setDestinationDistrictId(String destinationDistrictId) { 
        this.destinationDistrictId = destinationDistrictId; 
    }

    

    public List<CheckoutItemDto> getItems() { 
        return items; 
    }
    
    public void setItems(List<CheckoutItemDto> items) { 
        this.items = items; 
    }
}