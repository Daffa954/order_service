package com.example.order_service.DTO;

import java.util.List;

public class ShopOrderDto {
    private Long shopId;
    private ShippingCourierDto shippingCourier;
    private List<CheckoutItemDto> items;

    // Create getter setter

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public ShippingCourierDto getShippingCourier() {
        return shippingCourier;
    }

    public void setShippingCourier(ShippingCourierDto shippingCourier) {
        this.shippingCourier = shippingCourier;
    }

    public List<CheckoutItemDto> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemDto> items) {
        this.items = items;
    }

}