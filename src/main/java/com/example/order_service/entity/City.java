package com.example.order_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    private Long id; // ID dari RajaOngkir
    private String name;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

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

    public void setName(String name) {
        this.name = name;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

}