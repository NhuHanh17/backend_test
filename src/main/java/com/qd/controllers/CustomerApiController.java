package com.qd.controllers;

import com.qd.service.CartService;
import com.qd.service.CustomerService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerApiController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;
    
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
        response.put("data", customerService.getServiceSubItems(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getMyCart(Principal principal, @RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", cartService.getMyCart(principal.getName(), params));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cart/items/{sellableId}")
    public ResponseEntity<Map<String, Object>> addItemToCart(
            Principal principal, 
            @PathVariable("sellableId") Long sellableItemId,
            @RequestBody Map<String, Object> body) {
        int quantity = 1;
        if (body != null && body.containsKey("quantity")) {
            quantity = Integer.parseInt(body.get("quantity").toString());
        }
        cartService.addItemToCart(principal.getName(), sellableItemId, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
        response.put("data", null); 

        return ResponseEntity.ok(response);
    }

    @PutMapping("/cart/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @PathVariable("cartItemId") Long cartItemId, 
            @RequestBody Map<String, Object> body) {
        
        if (body == null || !body.containsKey("quantity")) {
            throw new RuntimeException("Thiếu dữ liệu số lượng - quantity trong Request Body!");
        }
        
        int quantity = Integer.parseInt(body.get("quantity").toString());
        cartService.updateCartItemQuantity(cartItemId, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cập nhật số lượng dòng sản phẩm thành công!");
        response.put("data", null);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(@PathVariable("cartItemId") Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã xóa ra khỏi giỏ thành công!");
        return ResponseEntity.ok(response);
    }

   



}
