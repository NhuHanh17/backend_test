/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.controllers;

import com.qd.dto.AdminActionRequest;
import com.qd.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qd.service.UserService;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author ADMIN
 */

@RestController
@RequestMapping("/api/admin/providers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminProviderController {
    @Autowired
    private UserService userService;


    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingProviders(@RequestParam Map<String, String> params) {
        Map<String, Object> data = userService.getAdminProvidersList(false, params);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/approved")
    public ResponseEntity<Map<String, Object>> getApprovedProviders(@RequestParam Map<String, String> params) {
        Map<String, Object> data = userService.getAdminProvidersList(true, params);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/rejected")
    public ResponseEntity<Map<String, Object>> getRejectedProviders(@RequestParam Map<String, String> params) {
        params.put("type", "rejected");
        
        Map<String, Object> data = userService.getAdminProvidersList(false, params); 
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<AuthResponse> approveProvider(@PathVariable("id") Long id) {
        AuthResponse response = userService.approveProvider(id);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AuthResponse> rejectProvider(
            @PathVariable("id") Long id,@RequestBody AdminActionRequest dto) { 
        AuthResponse response = userService.rejectProvider(id, dto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<AuthResponse> banProvider(
            @PathVariable("id") Long id,@RequestBody AdminActionRequest dto) {
        
        AuthResponse response = userService.banProvider(id, dto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
