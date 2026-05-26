/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto;
import org.springframework.web.multipart.MultipartFile;
/**
 *
 * @author User
 */

public class RegisterRequest {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private MultipartFile avatar; //File nhị phân nằm trong DTO
    private String roleType;
    
    //    -----Provider-----
    private String companyName;
    private String taxCode;
    private String hotline;
    private String businessAddress;

    public RegisterRequest() {}

    private RegisterRequest(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.avatar = builder.avatar;
        this.roleType = builder.roleType;
        
        this.companyName = builder.companyName;
        this.taxCode = builder.taxCode;
        this.hotline = builder.hotline;
        this.businessAddress = builder.businessAddress;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public MultipartFile getAvatar() { return avatar; }
    public void setAvatar(MultipartFile avatar) { this.avatar = avatar; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    //    -----Provider-----
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getHotline() { return hotline; }
    public void setHotline(String hotline) { this.hotline = hotline; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    
    public static class Builder {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String phone;
        private MultipartFile avatar;
        private String roleType;

        private String companyName;
        private String taxCode;
        private String hotline;
        private String businessAddress;

        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder avatar(MultipartFile avatar) { this.avatar = avatar; return this; }
        public Builder roleType(String roleType) { this.roleType = roleType; return this; } // 🎯 Đã vá hàm .roleType() nhen!
        // Provider Builder Methods
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder taxCode(String taxCode) { this.taxCode = taxCode; return this; }
        public Builder hotline(String hotline) { this.hotline = hotline; return this; }
        public Builder businessAddress(String businessAddress) { this.businessAddress = businessAddress; return this; }

        public RegisterRequest build() { return new RegisterRequest(this); }
    }

    public static Builder builder() { return new Builder(); }
}
