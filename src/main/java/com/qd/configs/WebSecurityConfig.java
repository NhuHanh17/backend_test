package com.qd.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity ///Quét @PreAuthorize và Custom Annotation
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http.cors(cors -> cors.disable())
        //     .csrf(csrf -> csrf.disable())
        //     .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
        //     .authorizeHttpRequests(auth -> auth
        //         .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
        //     );


            http.cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // 1. Nhóm Public (Công khai)

                .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/services/**", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/orders/webhook-callback")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()

                // 2. Nhóm Admin Site
                .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/api/analytics/admin/**")).hasRole("ADMIN")

                // 3. Nhóm Đối tác (Provider)
                .requestMatchers(new AntPathRequestMatcher("/api/provider/**")).hasRole("PROVIDER")

                // 4. Nhóm Khách hàng (Customer)
                .requestMatchers(new AntPathRequestMatcher("/api/cart/**")).hasRole("CUSTOMER")
                .requestMatchers(new AntPathRequestMatcher("/api/orders/customer/**")).hasRole("CUSTOMER")
                .requestMatchers(new AntPathRequestMatcher("/api/reviews", "POST")).hasRole("CUSTOMER")

                // 5. Nhóm Yêu cầu đăng nhập nói chung
                .requestMatchers(new AntPathRequestMatcher("/api/auth/profile/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).authenticated()

                // Tất cả các request phát sinh khác đều phải đăng nhập
                .anyRequest().authenticated()
            );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}