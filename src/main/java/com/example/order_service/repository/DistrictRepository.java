package com.example.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.order_service.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByCityId(Long cityId); 
}