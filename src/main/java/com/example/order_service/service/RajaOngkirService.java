package com.example.order_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.DTO.CartShippingRequest;
import com.example.order_service.DTO.CheckoutItemDto;

@Service
public class RajaOngkirService {

    // Ambil API Key dari .env (application.properties)
    @Value("${rajaongkir.key}")
    private String apiKey;

    @Value("${toko.active-couriers}")
    private String activeCouriers;

    private final RestTemplate restTemplate;

    public RajaOngkirService() {
        this.restTemplate = new RestTemplate();
    }

    // 1. Mengambil Daftar Kecamatan (District) berdasarkan City ID
    public Object getDistrictsByCityId(String cityId) {
        String url = "https://rajaongkir.komerce.id/api/v1/destination/district/" + cityId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Kita return sebagai Object murni agar struktur JSON asli dari Komerce
        // langsung diteruskan ke React
        ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Object.class);

        return response.getBody();
    }

    public Object calculateShippingOptions(CartShippingRequest request) {

        int totalWeight = 0;

        // 1. Kalkulasi total berat murni di sisi Backend
        if (request.getItems() != null) {
            for (CheckoutItemDto item : request.getItems()) {
                // Pastikan frontend mengirimkan weight dan quantity
                totalWeight += (item.getWeight() * item.getQuantity());
            }
        }

        // 2. Fallback: Jika data barang kosong/berat 0, set ke 1000 gram agar API
        // Komerce tidak error
        if (totalWeight <= 0) {
            totalWeight = 1000;
        }

        // 3. Tembak langsung ke fungsi calculateDomesticCost yang sudah kamu buat
        // sebelumnya
        // 3. Tembak langsung ke fungsi calculateDomesticCost
        return calculateDomesticCost(
                request.getOriginDistrictId(), // Ubah di baris ini
                request.getDestinationDistrictId(), // Ubah di baris ini
                totalWeight,
                activeCouriers);
    }

    // Ini fungsi lama milikmu, biarkan seperti ini
    public Object calculateDomesticCost(String origin, String destination, int weight, String couriers) {
        String url = "https://rajaongkir.komerce.id/api/v1/calculate/district/domestic-cost";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Key", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("origin", origin);
        body.add("destination", destination);
        body.add("weight", String.valueOf(weight));
        body.add("courier", couriers);
        body.add("price", "lowest");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 1. Ubah tipe tangkapan dari Object.class menjadi Map
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // 2. Lakukan pembatasan (Limit) maksimal 5 opsi
        if (responseBody != null && responseBody.containsKey("data")) {
            Object dataObject = responseBody.get("data");

            // Pastikan format "data" adalah sebuah List (Array)
            if (dataObject instanceof List) {
                List<?> dataList = (List<?>) dataObject;

                // Jika hasilnya lebih dari 5, potong array-nya dari index 0 sampai 5
                if (dataList.size() > 5) {
                    List<?> limitedList = dataList.subList(0, 5);
                    // Masukkan kembali array yang sudah dipotong ke dalam Map
                    responseBody.put("data", limitedList);
                }
            }
        }

        // 3. Kembalikan Map yang datanya sudah dipangkas
        return responseBody;
    }
}