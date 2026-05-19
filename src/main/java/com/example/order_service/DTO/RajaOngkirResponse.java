package com.example.order_service.DTO;

import java.util.Map;
import java.util.List;

public class RajaOngkirResponse<T> {
    private Map<String, Object> rajaongkir;

    @SuppressWarnings("unchecked")
    public List<T> getResults() {
        return (List<T>) rajaongkir.get("results");
    }
}
