/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qd.annotation.RequiresApprovedProvider;
import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.qd.service.ProviderService;
import com.qd.service.UserService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api/provider/services")
@RequiresApprovedProvider
public class ProviderApiController {

    // @Autowired
    // private UserService userService;

    @Autowired
    private ProviderService providerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyServices(
            Principal principal, @RequestParam Map<String, String> params) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", providerService.getMyServicesList(principal.getName(), params));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/{serviceType}")
    public ResponseEntity<Map<String, Object>> getMyServiceDetail(
            java.security.Principal principal, 
            @PathVariable("id") Long id,@PathVariable("serviceType") String serviceType) {
        
        String cleanType = serviceType.toUpperCase();
        if (cleanType.contains("TOUR")) {
            cleanType = "TOUR";
        } else if (cleanType.contains("HOTEL")) {
            cleanType = "HOTEL";
        } else if (cleanType.contains("TRANSPORT") || cleanType.contains("XE")) {
            cleanType = "TRANSPORT";
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", providerService.getMyServiceDetail(principal.getName(), id, cleanType));
        
        return ResponseEntity.ok(response);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Map<String, Object>> saveComprehensiveService(
            Principal principal,@RequestParam("data") String dataJson, 
            @RequestPart(value = "images", required = false) MultipartFile[] files) { 
        
        BaseComprehensiveRequest req;
        try {
            Map<String, Object> rawMap = objectMapper.readValue(dataJson, Map.class);
            String serviceType = String.valueOf(rawMap.get("serviceType")).toUpperCase();
            
            if (serviceType.contains("TOUR")) {
                req = objectMapper.readValue(dataJson, TourComprehensiveRequest.class);
            } else if (serviceType.contains("HOTEL")) {
                req = objectMapper.readValue(dataJson, HotelComprehensiveRequest.class);
            } else {
                req = objectMapper.readValue(dataJson, TransportComprehensiveRequest.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi định dạng chuỗi văn bản JSON gửi lên: " + e.getMessage());
        }

        Long serviceId = providerService.saveComprehensiveServiceInOneGo(principal.getName(), req, files);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lưu dịch vụ và upload ảnh lên Cloudinary thành công!");
        response.put("serviceId", serviceId);
        return ResponseEntity.ok(response);
    }
}