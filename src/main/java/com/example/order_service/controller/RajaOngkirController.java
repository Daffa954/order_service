package com.example.order_service.controller;

import com.example.order_service.DTO.APIResponse;
import com.example.order_service.DTO.CartShippingRequest;
import com.example.order_service.DTO.CostRequestDto;
import com.example.order_service.service.RajaOngkirService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rajaongkir")
public class RajaOngkirController {

    private final RajaOngkirService rajaOngkirService;

    public RajaOngkirController(RajaOngkirService rajaOngkirService) {
        this.rajaOngkirService = rajaOngkirService;
    }

    // Endpoint: GET http://localhost:8080/orderservice/rajaongkir/district/575
    @GetMapping("/district/{cityId}")
    public ResponseEntity<APIResponse<Object>> getDistricts(@PathVariable String cityId) {
        try {
            Object districts = rajaOngkirService.getDistrictsByCityId(cityId);
            return ResponseEntity.ok(new APIResponse<>(200, "Berhasil mengambil data kecamatan", districts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new APIResponse<>(400, "Gagal mengambil data: " + e.getMessage(), null));
        }
    }

    // Endpoint Baru: POST
    // http://localhost:8080/orderservice/rajaongkir/calculate-cart-options
    @PostMapping("/calculate-cart-options")
    public ResponseEntity<APIResponse<Object>> getShippingOptionsFromCart(@RequestBody CartShippingRequest request) {
        try {
            // Memanggil fungsi baru yang menghitung berat secara otomatis
            Object costResult = rajaOngkirService.calculateShippingOptions(request);

            return ResponseEntity.ok(new APIResponse<>(200, "Berhasil menghitung ongkir dari keranjang", costResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new APIResponse<>(400, "Gagal menghitung ongkir: " + e.getMessage(), null));
        }
    }
}
