package com.example.order_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.entity.City;
import com.example.order_service.entity.Province;
import com.example.order_service.repository.CityRepository;
import com.example.order_service.repository.ProvinceRepository;

import jakarta.transaction.Transactional;

@Service
public class LocationSeederService {

    @Value("${rajaongkir.key}")
    private String apiKey;

    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;

    public LocationSeederService(ProvinceRepository pr, CityRepository cr) {
        this.provinceRepository = pr;
        this.cityRepository = cr;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void seedLocations() {
        // 1. Guard: Jangan seed jika sudah ada data
        if (provinceRepository.count() > 0) {
            System.out.println("Data wilayah sudah ada. Seeding dilewati.");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            // --- STEP 1: PROVINSI ---
            String pUrl = "https://rajaongkir.komerce.id/api/v1/destination/province";
            ResponseEntity<Map> pRes = restTemplate.exchange(pUrl, HttpMethod.GET, entity, Map.class);

            // Ambil dari field "data" sesuai JSON Postman-mu
            List<Map<String, Object>> pList = (List<Map<String, Object>>) pRes.getBody().get("data");

            for (Map<String, Object> pMap : pList) {
                Province p = new Province();
                // Pakai "id" dan "name" sesuai JSON Postman
                p.setId(Long.valueOf(pMap.get("id").toString()));
                p.setProvinceName(pMap.get("name").toString());
                provinceRepository.save(p);
            }
            System.out.println("Seeding Provinsi Berhasil! Total: " + provinceRepository.count());

            // --- STEP 2: KOTA ---
            // Kita ambil data kota per provinsi menggunakan loop
            List<Province> provinces = provinceRepository.findAll();
            for (Province prov : provinces) {
                String cUrl = "https://rajaongkir.komerce.id/api/v1/destination/city/" + prov.getId();
                ResponseEntity<Map> cRes = restTemplate.exchange(cUrl, HttpMethod.GET, entity, Map.class);

                List<Map<String, Object>> cList = (List<Map<String, Object>>) cRes.getBody().get("data");

                if (cList != null) {
                    for (Map<String, Object> cMap : cList) {
                        City c = new City();
                        c.setId(Long.valueOf(cMap.get("id").toString())); // Gunakan "id"
                        c.setName(cMap.get("name").toString()); // Gunakan "name"
                        c.setProvince(prov);
                        cityRepository.save(c);
                    }
                }
                // Cetak progress agar kamu tahu aplikasi tidak hang
                System.out.println("Selesai seeding kota untuk provinsi: " + prov.getName());
            }
            System.out.println("Semua data wilayah berhasil disimpan!");

        } catch (Exception e) {
            System.err.println("Gagal Seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

}