package com.qd.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qd.pojo.Reviews;
import com.qd.service.ReviewService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/customer")
public class CustomerReviewApiController {
    @Autowired
    private ReviewService reviewService;


    @GetMapping("/services/{serviceId}/reviews")
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



    @PostMapping("/order-details/{orderDetailId}/reviews")
    public ResponseEntity<Map<String, Object>> postServiceReview(
            Principal principal,@PathVariable("orderDetailId") Long orderDetailId,
            @RequestBody Map<String, Object> body) {

        if (body == null || !body.containsKey("ratingStar")) {
            throw new RuntimeException("Thiếu  số sao đánh giá (ratingStar) trong Request Body!");
        }
        int ratingStar = Integer.parseInt(body.get("ratingStar").toString());
        String commentText = body.containsKey("commentText") ? body.get("commentText").toString() : "";
        reviewService.postUserReview(principal.getName(), orderDetailId, ratingStar, commentText);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng tải bài viết đánh giá chất lượng dịch vụ TravelVista thành công!");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }
}
