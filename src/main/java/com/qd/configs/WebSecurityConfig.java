// package com.qd.configs;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.EnableAspectJAutoProxy;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// @EnableAspectJAutoProxy ///Quét @PreAuthorize và Custom Annotation
// public class WebSecurityConfig {
//     @Autowired
//     private JwtAuthenticationFilter jwtAuthenticationFilter;

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         // http.cors(cors -> cors.disable())
//         //     .csrf(csrf -> csrf.disable())
//         //     .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//         //     .authorizeHttpRequests(auth -> auth
//         //         .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
//         //     );

//             http.cors(cors -> cors.disable())
//             .csrf(csrf -> csrf.disable())
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//             .authorizeHttpRequests(auth -> auth

//                 .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/api/services/**", "GET")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/api/orders/webhook-callback")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/api/services/cart/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/api/services/callback", "POST")).permitAll()

//                 // .requestMatchers(new AntPathRequestMatcher("/api/customer/services/*/reviews", "GET")).permitAll()

//                 .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
//                 .requestMatchers(new AntPathRequestMatcher("/api/analytics/admin/**")).hasRole("ADMIN")

//                 .requestMatchers(new AntPathRequestMatcher("/api/provider/**")).hasRole("PROVIDER")

//                 .requestMatchers(new AntPathRequestMatcher("/api/cart/**")).hasRole("CUSTOMER")
//                 .requestMatchers(new AntPathRequestMatcher("/api/orders/customer/**")).hasRole("CUSTOMER")
//                 .requestMatchers(new AntPathRequestMatcher("/api/reviews", "POST")).hasRole("CUSTOMER")
//                 .requestMatchers(new AntPathRequestMatcher("/api/customer/**")).hasRole("CUSTOMER")
//                 .requestMatchers(new AntPathRequestMatcher("/api/services/orders", "POST")).hasRole("CUSTOMER")

//                 .requestMatchers(new AntPathRequestMatcher("/api/auth/profile/**")).authenticated()
//                 .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).authenticated()

//                 .anyRequest().authenticated()
//             );
//         http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//         return http.build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }


package com.qd.configs;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/services/**", "GET")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/orders/webhook-callback")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/services/cart/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/services/callback", "POST")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/customer/services/*/reviews", "GET"))
                        .permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/analytics/admin/**")).hasRole("ADMIN")

                        .requestMatchers(new AntPathRequestMatcher("/api/provider/**")).hasRole("PROVIDER")

                        .requestMatchers(new AntPathRequestMatcher("/api/cart/**")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/orders/customer/**")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/reviews", "POST")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/customer/**")).hasRole("CUSTOMER")
                        .requestMatchers(new AntPathRequestMatcher("/api/services/orders", "POST")).hasRole("CUSTOMER")

                        .requestMatchers(new AntPathRequestMatcher("/api/auth/profile/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).authenticated()

                        .anyRequest().authenticated());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://travel-vista-frontend-ten.vercel.app"));

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}