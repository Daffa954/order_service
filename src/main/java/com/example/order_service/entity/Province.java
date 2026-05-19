package com.example.order_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "provinces")
public class Province {
    @Id
    private Long id; // ID dari RajaOngkir
    private String name;

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setProvinceName(String name) {
        this.name = name;
    }

}