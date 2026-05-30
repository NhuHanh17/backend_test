package com.qd.controllers;

import com.qd.service.CustomerService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerApiController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getServicesForHomepage(@RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", customerService.getServicesForCustomer(params));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getServiceMainDetail(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", customerService.getServiceMainDetail(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/sub-items")
    public ResponseEntity<Map<String, Object>> getServiceSubItemsAndReviews(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", customerService.getServiceSubItemsAndReviews(id));
        return ResponseEntity.ok(response);
    }



}
