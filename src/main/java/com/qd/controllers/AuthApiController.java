/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.controllers;

import com.qd.annotation.RateLimiter;
import com.qd.dto.AuthResponse;
import com.qd.dto.ChangePasswordRequest;
import com.qd.dto.LoginRequest;
import com.qd.dto.RegisterRequest;
import com.qd.dto.UserProfile;
import com.qd.service.UserService;
import com.qd.utils.JwtProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

//import org.springframework.web.bind.annotation.*;
/**
 *
 * @author User
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping(value = "/register", consumes = { "multipart/form-data" })
    @RateLimiter(requests = 3, seconds = 60)
    public ResponseEntity<AuthResponse> register(@ModelAttribute RegisterRequest registerRequest) {
        AuthResponse response = userService.register(registerRequest);

        if (response.isSuccess())
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest loginRequest)// @RequestBody la json thô
    {

        AuthResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (response.isSuccess()) {

            String token = jwtProvider.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Ma 401
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        if (authentication != null) {
            userService.logout(authentication.getName());
            response.put("message", "Đăng xuất tài khoản thành công! Phiên làm việc đã kết thúc.");
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Yêu cầu không hợp lệ!");
        return ResponseEntity.badRequest().body(response);
    }

    
    @GetMapping("/profile")
    public UserProfile getProfile(Authentication authentication) {
        return userService.getUserProfile(authentication.getName());
    }

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("image/jpeg", "image/jpg", "image/png",
            "image/webp");

    @PatchMapping("/profile")
    public ResponseEntity<?> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng chọn một file ảnh trước khi tải lên!");
            return ResponseEntity.badRequest().body(response);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("success", false);
            response.put("message", "Kích thước ảnh tối đa 5MB.");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXTENSIONS.contains(contentType.toLowerCase())) {
            response.put("success", false);
            response.put("message", "Định dạng file không hỗ trợ! Chỉ nhận .jpg, .jpeg, .png, .webp");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String avatarUrl = userService.updateUserAvatar(username, file);

            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công!");
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Hệ thống gặp sự cố: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody UserProfile dto,
            Authentication authentication) {
        String currentUserName = authentication.getName();
        AuthResponse response = userService.updateUserProfile(currentUserName, dto);
        if (!response.isSuccess())
            return ResponseEntity.badRequest().body(response);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/password")
    public ResponseEntity<AuthResponse> changePassword(
            @RequestBody ChangePasswordRequest dto,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        AuthResponse response = userService.changePassword(currentUsername, dto);

        return ResponseEntity.ok(response);
    }

}