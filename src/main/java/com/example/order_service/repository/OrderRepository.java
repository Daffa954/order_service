package com.example.order_service.repository;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Kosong saja! Spring otomatis membuatkan fungsi insert, select, delete.
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByTransactionId(String transactionId);

    @Query("SELECT o FROM Order o WHERE " +
           "(:shopId IS NULL OR o.shopId = :shopId) AND " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:start IS NULL OR o.orderDate >= :start) AND " +
           "(:end IS NULL OR o.orderDate <= :end)")
    Page<Order> filterOrdersForAdmin(
            @Param("shopId") Long shopId,
            @Param("status") String status, // Atau gunakan tipe Enum OrderStatus
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // Untuk Statistik Dashboard
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countOrdersByStatus(@Param("status") String status);
}