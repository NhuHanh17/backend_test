// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package com.qd.configs;

// import com.qd.pojo.Users;
// import com.qd.repository.UserRepository;
// import com.qd.utils.JwtProvider;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import java.util.Collections;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;

// /**
//  *
//  * @author ADMIN
//  */
// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter{
//     @Autowired
//     private JwtProvider jwtProvider;

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
//         try{
//             String bearerToken=request.getHeader("Authorization");
//             if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//                 String token = bearerToken.substring(7);      
//                 if (jwtProvider.validateToken(token)) {
//                     String username = jwtProvider.getUsernameFromJWT(token);
//                     Users user = userRepository.findByUsername(username);
//                     if (user != null && token.equals(user.getCurrentToken())) {
//                         String role = jwtProvider.getRoleFromJWT(token);
//                         if (role == null)  role = "ROLE_CUSTOMER";
//                         if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

//                         UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                                 username, null, Collections.singletonList(new SimpleGrantedAuthority(role)));

//                         SecurityContextHolder.getContext().setAuthentication(authentication);
//                     } else {
//                         SecurityContextHolder.clearContext();
//                     }
//                 }
//         }
//     }catch (Exception e) {   
//             SecurityContextHolder.clearContext();
//         }
//         filterChain.doFilter(request, response);
//     }
        
// }


package com.qd.configs;

import com.qd.pojo.Users;
import com.qd.repository.UserRepository;
import com.qd.utils.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String bearerToken = request.getHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                if (!jwtProvider.validateToken(token)) {
                    SecurityContextHolder.clearContext();

                    writeUnauthorizedResponse(
                            response,
                            "TOKEN_INVALID_OR_EXPIRED",
                            "Phiên đăng nhập đã hết hạn hoặc không hợp lệ"
                    );
                    return;
                }

                String username = jwtProvider.getUsernameFromJWT(token);
                Users user = userRepository.findByUsername(username);

                if (user == null || !token.equals(user.getCurrentToken())) {
                    SecurityContextHolder.clearContext();

                    writeUnauthorizedResponse(
                            response,
                            "TOKEN_REPLACED",
                            "Tài khoản đã đăng nhập ở thiết bị khác"
                    );
                    return;
                }

                String role = jwtProvider.getRoleFromJWT(token);

                if (role == null) {
                    role = "ROLE_CUSTOMER";
                }

                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(role)
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            writeUnauthorizedResponse(
                    response,"TOKEN_INVALID_OR_EXPIRED",
                    "Phiên đăng nhập đã hết hạn hoặc không hợp lệ"
            );
        }
    }

    private void writeUnauthorizedResponse(
            HttpServletResponse response,
            String code,
            String message
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(String.format(
                "{\"success\":false,\"code\":\"%s\",\"message\":\"%s\"}",
                code,
                message
        ));
    }
}
