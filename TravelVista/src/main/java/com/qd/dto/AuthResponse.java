/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto;

import com.qd.pojo.Users;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author User
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl; 
    private String roleName;
    private Date createdAt;

    private AuthResponse(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.token = builder.token;
        this.userId = builder.userId;
        this.username = builder.username;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.avatarUrl = builder.avatarUrl;
        this.roleName = builder.roleName;
        this.createdAt = builder.createdAt;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRoleName() { return roleName; }
    public Date getCreatedAt() { return createdAt; }

    public static class Builder {
        private boolean success;
        private String message;
        private String token;
        private Long userId;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private String avatarUrl;
        private String roleName;
        private Date createdAt;

        public Builder success(boolean success) { this.success = success; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder token(String token) { this.token = token; return this; }

        public Builder userDetail(Users user) {
            if (user != null) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.fullName = user.getFullName();
                this.email = user.getEmail();
                this.phone = user.getPhone();
                this.avatarUrl = user.getAvatarUrl();
                this.createdAt = user.getCreatedAt();
                if (user.getRoleId() != null) {
                    this.roleName = user.getRoleId().getRoleName();
                }
            }
            return this;
        }

        public AuthResponse build() { return new AuthResponse(this); }
    }

    public static Builder builder() { return new Builder(); }
}