/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.service;

import com.qd.dto.AdminActionRequest;
import com.qd.dto.AuthResponse;
import com.qd.dto.RegisterRequest;
import com.qd.dto.UserProfile;
import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.pojo.Users;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
public interface UserService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(String username,String password);
    UserProfile getUserProfile(String username);
    String updateUserAvatar(String username, MultipartFile file);
    AuthResponse updateUserProfile(String username, UserProfile req);
    AuthResponse changePassword(String username, com.qd.dto.ChangePasswordRequest req);
    Map getAdminProvidersList(boolean isApproved, java.util.Map<String, String> params);
    AuthResponse approveProvider(Long id);
    AuthResponse rejectProvider(Long id, AdminActionRequest req);
    AuthResponse banProvider(Long id, AdminActionRequest req);
    Users findByUsername(String username);
    Users findById(Long id);
    // Map<String, Object> getMyServicesList(String username, java.util.Map<String, String> params);
    // Object getMyServiceDetail(String username, Long id, String typeStr);
    // Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req);

}
