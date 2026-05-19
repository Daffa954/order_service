package com.example.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order_service.entity.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
}

