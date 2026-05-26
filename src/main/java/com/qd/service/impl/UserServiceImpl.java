/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.qd.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qd.dto.AuthResponse;
import com.qd.dto.RegisterRequest;
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
    public AuthResponse register(RegisterRequest req) {
        //Check trùng lặp tài khoản và email trên hệ thống bảng Users
        if (userRepository.isExistByUsername(req.getUsername())) {
            return AuthResponse.builder().success(false).message("Tên tài khoản này đã tồn tại!").build();
        }
        if (userRepository.isExistByEmail(req.getEmail())) {
            return AuthResponse.builder().success(false).message("Email này đã được đăng ký rồi!").build();
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

        Roles role = new Roles();
        if (isProvider) {
            role.setId(2L); // 2L ID PROVIDER 
        } else {
            role.setId(3L); // 3L là ID CUSTOMER 
        }
        user.setRoleId(role);
        
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    req.getAvatar().getBytes(),
                    ObjectUtils.emptyMap()
            );
            String cloudUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(cloudUrl);
        } catch (IOException e) {
            return AuthResponse.builder().success(false).message("Lỗi hỏa hoạn khi upload ảnh đại diện lên Cloudinary!").build();
        }
        
        userRepository.save(user);

        if (isProvider) {
            Providers provider = new Providers();
            provider.setUserId(user);
            provider.setCompanyName(req.getCompanyName());
            provider.setTaxCode(req.getTaxCode());
            provider.setHotline(req.getHotline());
            provider.setBusinessAddress(req.getBusinessAddress());
            provider.setIsApproved(false);
            provider.setApprovedAt(null);
            providerRepository.save(provider);
        }
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

}