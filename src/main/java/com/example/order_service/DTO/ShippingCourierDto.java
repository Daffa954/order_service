package com.example.order_service.DTO;


public class ShippingCourierDto {
    private String name;
    private String service;
    private Double cost;
    private String etd;

    // Getter dan Setter Manual (Sesuai gaya CheckoutItemDto kamu)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }
}