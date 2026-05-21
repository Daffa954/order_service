package com.example.order_service.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.order_service.enums.OrderStatus;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // Nama tabel di PostgreSQL
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "shop_name")
    private String shopName;

    @Enumerated(EnumType.STRING) // Simpan sebagai String di DB
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "shipping_cost")
    private Double shippingCost;

    @Column(name = "final_total_price")
    private Double finalTotalPrice;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "courier_name")
    private String courierName; // Contoh: "Wahana"

    @Column(name = "courier_code")
    private String courierCode; // Contoh: "wahana" atau "jne" (Penting untuk API)

    @Column(name = "courier_service")
    private String courierService; // Contoh: "Ekonomis" atau "REG"

    @Column(name = "etd")
    private String etd; // Contoh: "3 day" (Pakai String saja karena format Komerce bisa "2-3 hari")

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_province")
    private String shippingProvince;

    @Column(name = "shipping_district")
    private String shippingDistrict;

    @Column(name = "shipping_city_id")
    private Long shippingCityId; // Untuk referensi API Komerce/RajaOngkir

    @Column(name = "shipping_province_id")
    private Long shippingProvinceId; // Untuk referensi API Komerce/RajaOngki

     @Column(name = "shipping_district_id")
    private Long shippingDistrictId; // Untuk referensi API Komerce/RajaOngki



    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // foreign key tabel order_items

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // REVISI: 1 Order bisa punya banyak riwayat Payment (percobaan bayar Midtrans)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // Constructor kosong wajib untuk Spring JPA
    public Order() {
    }

    // Getter dan Setter
    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // get order items
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    // set order items
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // get payments
    public List<Payment> getPayments() {
        return payments;
    }

    // set payments
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getFinalTotalPrice() {
        return finalTotalPrice;
    }

    public void setFinalTotalPrice(Double finalTotalPrice) {
        this.finalTotalPrice = finalTotalPrice;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingProvince() {
        return shippingProvince;
    }

    public void setShippingProvince(String shippingProvince) {
        this.shippingProvince = shippingProvince;
    }

    public String getShippingDistrict() {
        return shippingDistrict;
    }

    public void setShippingDistrict(String shippingDistrict) {
        this.shippingDistrict = shippingDistrict;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // create getter and setter for courierName, courierCode, courierService, etd
    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierCode() {
        return courierCode;
    }

    public void setCourierCode(String courierCode) {
        this.courierCode = courierCode;
    }

    public String getCourierService() {
        return courierService;
    }

    public void setCourierService(String courierService) {
        this.courierService = courierService;
    }

    public String getEtd() {
        return etd;
    }   

    public void setEtd(String etd) {
        this.etd = etd;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }





}