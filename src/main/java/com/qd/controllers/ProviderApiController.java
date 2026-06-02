/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qd.annotation.CheckOwnership;
import com.qd.annotation.CheckServiceOwnership;
import com.qd.annotation.RequiresApprovedProvider;
import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;
import com.qd.dto.provider.UpdateImagesRequest;
import com.qd.dto.provider.UpdateSubItemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.qd.service.ProviderService;
import com.qd.service.UserService;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            @PathVariable("id") Long id, @PathVariable("serviceType") String serviceType) {

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

    // POST /{id}/{serviceType} -> Thêm 1 item con 

    @PostMapping("/{id}/{serviceType}")
    @CheckServiceOwnership(paramName = "id")
    public ResponseEntity<Map<String, Object>> addSubItem(
            Principal principal,
            @PathVariable("id") Long id,
            @PathVariable("serviceType") String serviceType,
            @RequestParam("data") String dataJson) {

        String cleanType = serviceType.toUpperCase();
        if (cleanType.contains("TOUR"))
            cleanType = "TOUR";
        else if (cleanType.contains("HOTEL"))
            cleanType = "HOTEL";
        else
            cleanType = "TRANSPORT";

        BaseComprehensiveRequest req = null;
        try {
            Map<String, Object> rawMap = objectMapper.readValue(dataJson, Map.class);
            if ("TOUR".equals(cleanType)) {
                req = objectMapper.readValue(dataJson, TourComprehensiveRequest.class);
            } else if ("HOTEL".equals(cleanType)) {
                req = objectMapper.readValue(dataJson, HotelComprehensiveRequest.class);
            } else {
                req = objectMapper.readValue(dataJson, TransportComprehensiveRequest.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cấu trúc dữ liệu JSON con gửi lên: " + e.getMessage());
        }

        providerService.addSubItemCustom(principal.getName(), id, cleanType, req);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Đã nạp thêm phân loại con lẻ mới vào bài đăng gốc thành công!"));
    }

    // PATCH /{id}/{serviceType}/{subItemId} -> Thay đổi trạng thái SUSPENDED 
    @DeleteMapping("/{id}/{serviceType}/{subItemId}")
    @CheckServiceOwnership(paramName = "id")
    public ResponseEntity<Map<String, Object>> deleteSubItem(
            Principal principal,
            @PathVariable("id") Long id,
            @PathVariable("serviceType") String serviceType,
            @PathVariable("subItemId") Long subItemId) {

        String cleanType = serviceType.toUpperCase();
        if (cleanType.contains("TOUR"))
            cleanType = "TOUR";
        else if (cleanType.contains("HOTEL"))
            cleanType = "HOTEL";
        else
            cleanType = "TRANSPORT";

        providerService.softDeleteSubItem(principal.getName(), id, cleanType, subItemId);
        return ResponseEntity.ok(Map.of("success", true, "message",
                "Đã tạm dừng mở bán và xóa mềm phân loại con (SUSPENDED) thành công!"));
    }

    @PutMapping("/{serviceId}/{serviceType}/{subItemId}")
    @CheckServiceOwnership(paramName = "serviceId")
    public ResponseEntity<?> updateSubItem(
            Principal principal,
            @PathVariable("serviceId") Long serviceId,
            @PathVariable("serviceType") String serviceType,
            @PathVariable("subItemId") Long subItemId,

            @RequestBody UpdateSubItemRequest req) {

        providerService.updateSubItem(
                principal.getName(),
                serviceId,
                serviceType,
                subItemId,
                req);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Cập nhật item thành công!"));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Map<String, Object>> saveComprehensiveService(
            Principal principal, @RequestParam("data") String dataJson,
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


    @PatchMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @CheckServiceOwnership(paramName = "id")
    public ResponseEntity<Map<String, Object>> patchServiceComprehensive(
            Principal principal,
            @PathVariable("id") Long id,
            @RequestParam(value = "status", required = false) String status,
            @RequestPart(value = "data", required = false) UpdateImagesRequest updateImagesReq,
            @RequestPart(value = "images", required = false) MultipartFile[] files) {

        List<Long> retainImageIds = (updateImagesReq != null) ? updateImagesReq.getRetainImageIds() : null;
        boolean hasImagesPayload = (retainImageIds != null && !retainImageIds.isEmpty()) || (files != null && files.length > 0);
        boolean hasStatusPayload = (status != null && !status.trim().isEmpty());
                
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", null);

        if (hasImagesPayload && hasStatusPayload) {
            providerService.updateServiceImagesAndStatusComprehensive(
                    principal.getName(), id, retainImageIds, files, status);
            response.put("message", "Đồng bộ hóa collab: Cập nhật album ảnh và chuyển trạng thái bài viết thành công!");
        }else if (hasImagesPayload) {
            providerService.updateServiceImages(principal.getName(), id, retainImageIds, files);
            response.put("message", "Cập nhật album ảnh và tái lập cờ hình đại diện thành công!");
        }else if (hasStatusPayload) {
            providerService.updateServiceStatus(principal.getName(), id, status);
            response.put("message", "Chuyển trạng thái bài viết sang " + status.toUpperCase() + " thành công!");
        } else {
            response.put("message", "Không có dữ liệu nào được thay đổi.");
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @CheckServiceOwnership(paramName = "id")
    public ResponseEntity<Map<String, Object>> updateService(
            Principal principal, @PathVariable("id") Long id, @RequestParam("data") String dataJson) {

        BaseComprehensiveRequest req = null;
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
            throw new RuntimeException("Lỗi cấu trúc dữ liệu JSON đầu vào: " + e.getMessage());
        }

        providerService.updateComprehensiveService(principal.getName(), id, req);
        return ResponseEntity.ok(Map.of("success", true, "message",
                "Đã lưu cập nhật thành công! Bài đăng đã được kích hoạt hiển thị công khai trên sàn lữ hành!"));
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getProviderOrders(
            Principal principal, @RequestParam Map<String, String> params) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", providerService.getOrdersByProvider(principal.getName(), params));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> getProviderOrderDetail(
            Principal principal, @PathVariable("id") Long orderId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", providerService.getProviderOrderDetail(principal.getName(), orderId));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProviderStats(
            Principal principal, 
            @RequestParam(value = "period", defaultValue = "month") String period) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", providerService.getProviderDashboardStats(principal.getName(), period));
        
        return ResponseEntity.ok(response);
    }

}