package com.example.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order_service.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
}