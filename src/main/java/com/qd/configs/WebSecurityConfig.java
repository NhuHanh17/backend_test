package com.qd.configs;

import java.util.List;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAspectJAutoProxy /// Quét @PreAuthorize và Custom Annotation
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Kích hoạt CORS và cấu hình theo Bean corsConfigurationSource() bên dưới
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dự án sử dụng Token (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép tất cả các Preflight Request (OPTIONS) đi qua tự do
                        // Điều này cực kỳ quan trọng để trình duyệt không bị chặn và gây lỗi 404 ảo
                        .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()

                        // 2. Nhóm Public (Ai cũng có thể truy cập mà không cần Token)
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/services/**", "GET")).permitAll() // Cho phép
                                                                                                           // xem danh
                                                                                                           // sách dịch
                                                                                                           // vụ công
                                                                                                           // khai
                        .requestMatchers(new AntPathRequestMatcher("/api/orders/webhook-callback")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()

                        // 3. Nhóm Admin Site (Yêu cầu quyền ADMIN)
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/analytics/admin/**")).hasRole("ADMIN")

                        // 4. Nhóm Đối tác (Yêu cầu quyền PROVIDER)
                        .requestMatchers(new AntPathRequestMatcher("/api/provider/**")).hasRole("PROVIDER")

                        // 5. Nhóm Khách hàng (Yêu cầu quyền CUSTOMER)
                        .requestMatchers(new AntPathRequestMatcher("/api/cart/**")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/orders/customer/**")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/reviews", "POST")).hasRole("CUSTOMER")

                        // 6. Nhóm Yêu cầu đăng nhập nói chung (Bất kỳ ai đăng nhập thành công đều vào
                        // được)
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/profile/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).authenticated()

                        // Tất cả các request phát sinh khác chưa khai báo ở trên đều phải đăng nhập
                        .anyRequest().authenticated());

        // Đính kèm Filter kiểm tra JWT trước khi request đi vào Filter xác thực mặc
        // định của Spring
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Đăng ký toàn bộ các Domain được phép gọi trực tiếp vào hệ thống Backend này
        config.setAllowedOrigins(List.of(
                "http://localhost:3000", // Môi trường phát triển Local của Frontend
                "https://travel-vista-frontend-ten.vercel.app/" // Trang Frontend chính thức chạy trên Vercel // chéo)
        ));

        // Cho phép các phương thức HTTP cơ bản
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"));

        // Cho phép các Header quan trọng, đặc biệt là Authorization (chứa JWT Token)
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"));

        // Bắt buộc bằng true nếu Frontend cần gửi kèm Cookie hoặc Header Authorization
        // (JWT)
        config.setAllowCredentials(true);

        // Áp dụng cấu hình CORS này cho mọi đường dẫn (/**) trong hệ thống
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng thuật toán BCrypt để mã hóa mật khẩu người dùng trong DB
        return new BCryptPasswordEncoder();
    }
}