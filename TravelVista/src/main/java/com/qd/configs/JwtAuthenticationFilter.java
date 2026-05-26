/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.configs;

import com.qd.utils.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author ADMIN
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        try{
            String bearerToken=request.getHeader("Authoiation");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                // Cắt bỏ 7 ký tự đầu "Bearer " để lấychuỗi Token thô
                String token = bearerToken.substring(7);      
                // Token vàoJwtProvider để kiểm tra hạn dùng + chữ ký
                if (jwtProvider.validateToken(token)) {
                    // Nếu chuẩn đét, lệnh chạy tiếp xuống đây để móc tên tài khoản ra
                    String username = jwtProvider.getUsernameFromJWT(token);

                    // Tạm thời gán cứng quyền hoặc bóc quyền từ Token ra để test thô
                    String role = "ROLE_CUSTOMER";

                    // Khởi tạo lệnh Authentication của Spring Security
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                    // Nạp vào hệ thống tổng ContextHolder để báo hợp lệ"
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
        }
    }catch (Exception e) {   
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
        
}

