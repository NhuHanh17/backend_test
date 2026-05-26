package com.qd.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth

    .requestMatchers(
        AntPathRequestMatcher.antMatcher("/"),
        AntPathRequestMatcher.antMatcher("/**"),
        AntPathRequestMatcher.antMatcher("/api/services/**"),
        AntPathRequestMatcher.antMatcher("/api/auth/**")
    ).permitAll()

    .anyRequest().authenticated()
);
            
//            .authorizeHttpRequests(auth -> auth
//
//                .requestMatchers(
//                    AntPathRequestMatcher.antMatcher("/api/services/**"),
//                    AntPathRequestMatcher.antMatcher("/api/auth/**")
//                ).permitAll()
//
//                .requestMatchers(
//                    AntPathRequestMatcher.antMatcher("/api/admin/**")
//                ).hasRole("ADMIN")
//
//                .requestMatchers(
//                    AntPathRequestMatcher.antMatcher("/api/provider/**")
//                ).hasRole("PROVIDER")
//
//                .requestMatchers(
//                    AntPathRequestMatcher.antMatcher("/api/cart/**"),
//                    AntPathRequestMatcher.antMatcher("/api/orders/**")
//                ).hasRole("CUSTOMER")
//
//                .anyRequest().authenticated()
//            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}