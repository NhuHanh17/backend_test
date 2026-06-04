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

import com.qd.pojo.Favorites;
import com.qd.pojo.Reviews;
import com.qd.pojo.Services;
import com.qd.pojo.Users;
import com.qd.service.CategoryService;
import com.qd.service.FavoriteService;
import com.qd.service.ReviewService;
import com.qd.service.UserService;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/customer")
public class CustomerUtilsApiController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;
    



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

    @GetMapping("/favorites")
    public ResponseEntity<Map<String, Object>> getMyFavoriteList(
            Principal principal,@RequestParam Map<String, String> params) { 
        try {
            Users user = userService.findByUsername(principal.getName());
            List<Favorites> favList = favoriteService.getMyFavorites(user, params);

            List<Map<String, Object>> data = favList.stream().map(fav -> {
                Services s = fav.getServices(); 
                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", s.getId());
                map.put("name", s.getName());
                map.put("serviceType", s.getServiceType().toString());
                map.put("averageRating", s.getAverageRating());
                map.put("reviewCount", s.getReviewCount());
                map.put("providerName", s.getProviderId().getCompanyName()); 
                map.put("likedAt", fav.getCreatedAt().getTime());
                return map;
            }).collect(Collectors.toList());

            String currentPage = params.getOrDefault("page", "1");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "currentPage", Integer.parseInt(currentPage),
                "data", data
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/favorites/{serviceId}")
    public ResponseEntity<Map<String, Object>> toggleMyFavorite(
            Principal principal, @PathVariable("serviceId") Long serviceId) {
        try {
            Users user = userService.findByUsername(principal.getName());
            String statusResult = favoriteService.toggleFavoriteService(user, serviceId);

            String msg = statusResult.equals("Yêu thích") 
                    ? "Đã thêm dịch vụ vào danh sách yêu thích thành công!" 
                    : "Đã xóa dịch vụ khỏi danh sách yêu thích!";

            return ResponseEntity.ok(Map.of(
                "success", true,
                "status", statusResult, 
                "message", msg
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories(@RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = categoryService.getCates(params);
        response.put("success", true);
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }


}
