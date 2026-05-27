/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.qd.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qd.dto.AuthResponse;
import com.qd.dto.RegisterRequest;
import com.qd.dto.UserProfile;
import com.qd.pojo.Providers;
import com.qd.pojo.Roles;
import com.qd.pojo.Users;
import com.qd.repository.ProviderRepository;
import com.qd.repository.UserRepository;
import com.qd.service.UserService;
import com.qd.utils.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author User
 */
@Service
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private Cloudinary cloudinary;
    
    @Override
    @Transactional
//    public AuthResponse register(RegisterRequest req) {
//        //Check trùng lặp tài khoản và email trên hệ thống bảng Users
//        if (userRepository.isExistByUsername(req.getUsername())) {
//            return AuthResponse.builder().success(false).message("Tên tài khoản này đã tồn tại!").build();
//        }
//        if (userRepository.isExistByEmail(req.getEmail())) {
//            return AuthResponse.builder().success(false).message("Email này đã được đăng ký rồi!").build();
//        }
//
//        if (req.getAvatar() == null || req.getAvatar().isEmpty()) {
//            return AuthResponse.builder().success(false).message("Ảnh đại diện là bắt buộc!").build();
//        }
//       
//        Users user = new Users();
//        user.setUsername(req.getUsername());
//        user.setFullName(req.getFullName());
//        user.setEmail(req.getEmail());
//        user.setPhone(req.getPhone());
//        
//        user.setPassword(passwordEncoder.encode(req.getPassword()));
//        user.setIsActive(true);
//        user.setCreatedAt(new Date());
//
//        long targetRoleId = isProvider ? 2L : 3L;
//        Roles role = userRepository.findRoleById(targetRoleId);
//        user.setRoleId(role);
//        
//        boolean isProvider = "PROVIDER".equalsIgnoreCase(req.getRoleType());
//        if (isProvider) {
//            if (req.getCompanyName() == null || req.getCompanyName().isEmpty() ||
//                req.getTaxCode() == null || req.getTaxCode().isEmpty() ||
//                req.getHotline() == null || req.getHotline().isEmpty() ||
//                req.getBusinessAddress() == null || req.getBusinessAddress().isEmpty()) {
//                return AuthResponse.builder().success(false).message("Đăng ký đối tác bắt buộc phải nhập đủ thông tin Doanh nghiệp!").build();
//            }
//
//            if (providerRepository.isExistsByCompanyName(req.getCompanyName())) {
//                return AuthResponse.builder().success(false).message("Tên Công ty/Doanh nghiệp này đã được đăng ký!").build();
//            }
//            if (providerRepository.isExistsByTaxCode(req.getTaxCode())) {
//                return AuthResponse.builder().success(false).message("Mã số thuế đã tồn tại trên hệ thống!").build();
//            }
//        }
//        
//        try {
//            Map uploadResult = cloudinary.uploader().upload(
//                    req.getAvatar().getBytes(),
//                    ObjectUtils.emptyMap()
//            );
//            String cloudUrl = (String) uploadResult.get("secure_url");
//            user.setAvatarUrl(cloudUrl);
//        } catch (IOException e) {
//            return AuthResponse.builder().success(false).message("Lỗi khi upload ảnh đại diện lên Cloudinary!").build();
//        }
//        
//        userRepository.save(user);
//
//        if (isProvider) {
//            Providers provider = new Providers();
//            provider.setUserId(user);
//            provider.setCompanyName(req.getCompanyName());
//            provider.setTaxCode(req.getTaxCode());
//            provider.setHotline(req.getHotline());
//            provider.setBusinessAddress(req.getBusinessAddress());
//            provider.setIsApproved(false);
//            provider.setApprovedAt(null);
//            providerRepository.save(provider);
//        }
//        String msg = isProvider ? "Đăng ký hồ sơ thành công. Vui lòng đợi Admin phê duyệt!"
//                                : "Đăng ký tài khoản thành công!";
//        return AuthResponse.builder().success(true).message(msg).build();
//    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.isExistByUsername(req.getUsername())) {
            return AuthResponse.builder().success(false).message("Tên tài khoản này đã tồn tại!").build();
        }
        if (userRepository.isExistByEmail(req.getEmail())) {
            return AuthResponse.builder().success(false).message("Email này đã được đăng ký rồi!").build();
        }
        if (userRepository.isExistByPhone(req.getPhone())) {
            return AuthResponse.builder().success(false).message("Số điện thoại này đã được đăng ký rồi!").build();
        }
        if (req.getAvatar() == null || req.getAvatar().isEmpty()) {
            return AuthResponse.builder().success(false).message("Ảnh đại diện là bắt buộc!").build();
        }
        boolean isProvider = "PROVIDER".equalsIgnoreCase(req.getRoleType());
        
        if (isProvider) {
            if (req.getCompanyName() == null || req.getCompanyName().isEmpty() ||
                req.getTaxCode() == null || req.getTaxCode().isEmpty() ||
                req.getHotline() == null || req.getHotline().isEmpty() ||
                req.getBusinessAddress() == null || req.getBusinessAddress().isEmpty()) {
                return AuthResponse.builder().success(false).message("Đăng ký đối tác bắt buộc phải nhập đủ thông tin Doanh nghiệp!").build();
            }

            if (providerRepository.isExistsByCompanyName(req.getCompanyName())) {
                return AuthResponse.builder().success(false).message("Tên Công ty/Doanh nghiệp này đã được đăng ký!").build();
            }
            if (providerRepository.isExistsByTaxCode(req.getTaxCode())) {
                return AuthResponse.builder().success(false).message("Mã số thuế đã tồn tại trên hệ thống!").build();
            }
        }

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(new Date());

        // Bốc Role chính thống từ DB lên gài vào để tránh lỗi Transient Role
        long targetRoleId = isProvider ? 2L : 3L;
        Roles role = userRepository.findRoleById(targetRoleId);
        user.setRoleId(role);

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    req.getAvatar().getBytes(),
                    ObjectUtils.emptyMap()
            );
            String cloudUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(cloudUrl);
        } catch (IOException e) {
            // Spring bắt buộc nếu muốn rollback tự động thì đoạn này phải ném ra RuntimeException
            throw new RuntimeException("Lỗi khi upload ảnh đại diện lên Cloudinary!");
        }

        if (isProvider) {
            Providers provider = new Providers();
            provider.setCompanyName(req.getCompanyName());
            provider.setTaxCode(req.getTaxCode());
            provider.setHotline(req.getHotline());
            provider.setBusinessAddress(req.getBusinessAddress());
            provider.setIsApproved(false);
            provider.setApprovedAt(null);
            
            provider.setUserId(user);   
            user.setProviders(provider); 
        }
        userRepository.save(user);
        String msg = isProvider ? "Đăng ký hồ sơ thành công. Vui lòng đợi Admin phê duyệt!"
                                : "Đăng ký tài khoản thành công!";
        return AuthResponse.builder().success(true).message(msg).build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(String username, String password) {
        Users user = userRepository.findByUsername(username);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return AuthResponse.builder().success(false)
                    .message("Tài khoản hoặc mật khẩu không chính xác!").build();
        }
        if (user.getIsActive() == null || !user.getIsActive()) {
            return AuthResponse.builder().success(false)
                    .message("Tài khoản này hiện đang bị khoá!").build();
        }
        String roleName = user.getRoleId().getRoleName(); 
        String token = jwtProvider.generateToken(user.getUsername(), roleName);
        return AuthResponse.builder()
                .success(true)
                .message("Đăng nhập Vista Travel thành công!")
                .token(token)
                .userDetail(user)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getUserProfile(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        }
        UserProfile profile = new UserProfile();
        profile.setUsername(user.getUsername());
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setPhone(user.getPhone());
        profile.setAvatarUrl(user.getAvatarUrl());
        profile.setRoleName(user.getRoleId().getRoleName());
        
        if (user.getProviders() != null) {
            Providers provider = user.getProviders();
            profile.setCompanyName(provider.getCompanyName());
            profile.setTaxCode(provider.getTaxCode());
            profile.setHotline(provider.getHotline());
            profile.setBusinessAddress(provider.getBusinessAddress());
            profile.setIsApproved(provider.getIsApproved());
        }
        
        return profile;
    }

    @Override
    public String updateUserAvatar(String username, MultipartFile file) {
        Users user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        
        if (user.getIsActive() != null && !user.getIsActive()) 
            throw new RuntimeException("Tài khoản này hiện đang bị khóa hoạt động, không thể sửa đổi hồ sơ!");
        

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );
            String cloudUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(cloudUrl);
            userRepository.save(user);
            return cloudUrl;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh đại diện lên Cloudinary!");
        }
    }


}