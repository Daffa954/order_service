package com.example.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "districts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District {
    @Id
    private Long id; // Menggunakan ID asli dari RajaOngkir/Komerce

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "name")
    private String name;

    // Getter
    public long getCityId() {
        return cityId;
    }

    // Setter
    public void setCityId(City city) {
        this.cityId = city.getId();
    }

    // Getter
    public String getName() {
        return name;
    }

    // Setter
    public void setName(String name) {
        this.name = name;
    }
    
}