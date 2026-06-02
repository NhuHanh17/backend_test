package com.qd.controllers;

import com.qd.dto.customer.CheckoutRequest;
import com.qd.pojo.Reviews;
import com.qd.pojo.Users;
import com.qd.service.CartService;
import com.qd.service.CheckoutService;
import com.qd.service.CustomerService;
import com.qd.service.ReviewService;
import com.qd.service.UserService;
import com.qd.service.impl.CheckoutServiceImpl;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getServicesForHomepage(@RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", customerService.getServicesForCustomer(params));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceId}/reviews")
    public ResponseEntity<Map<String, Object>> getServiceReviews(
            @PathVariable("serviceId") Long serviceId,
            @RequestParam Map<String, String> params) { 
        
        List<Reviews> reviewsList = reviewService.getReviewsByServicePaged(serviceId, params);
        
        List<Map<String, Object>> reviewsFeedback = reviewsList.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("reviewId", r.getId());
            map.put("clientName", r.getUserId().getFullName());
            map.put("reviewDate", r.getCreatedAt().getTime());
            map.put("commentText", r.getComment());
            map.put("ratingStar", r.getRating());
            
            map.put("subItemSnapshot", r.getOrderDetailId().getServiceNameSnapshot());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "customerReviewsFeedback", reviewsFeedback,
            "page", params.getOrDefault("page", "1")
        ));
        
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getServiceMainDetail(@PathVariable("id") Long id,Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        String currentUsername = (principal != null) ? principal.getName() : "";
        response.put("data", customerService.getServiceMainDetail(id, currentUsername));
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

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> processPlaceOrder(
            Principal principal, @RequestBody CheckoutRequest req) {
        try {
            Users buyer = userService.findByUsername(principal.getName());
            Map<String, Object> checkoutResult = checkoutService.executeCheckout(buyer, req);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", checkoutResult,
                "message", "Checkout TravelVista thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
   
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        try {
            Long orderId = Long.parseLong(payload.get("orderId").toString());
            String transactionRef = payload.get("transactionReference").toString();
            String responseCode = payload.get("responseCode").toString(); // "00" là thành công bên MoMo

            if ("00".equals(responseCode) || "SUCCESS".equalsIgnoreCase(responseCode)) {
                checkoutService.fulfillOrderAfterPayment(orderId, transactionRef);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã nhận dữ liệu IPN/Webhook thành công! Đơn hàng đã chuyển trạng thái sang PAY!"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Giao dịch thất bại!"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }



}
