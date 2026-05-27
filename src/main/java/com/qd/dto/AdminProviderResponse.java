/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class AdminProviderResponse {
    private Long providerId;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String companyName;
    private String taxCode;
    private String hotline;
    private String businessAddress;
    private Boolean isApproved;
    private Date createdAt;

    public AdminProviderResponse(com.qd.pojo.Providers p) {
        if (p != null) {
            this.providerId = p.getId();
            this.companyName = p.getCompanyName();
            this.taxCode = p.getTaxCode();
            this.hotline = p.getHotline();
            this.businessAddress = p.getBusinessAddress();
            this.isApproved = p.getIsApproved();
            
            if (p.getUserId() != null) {
                this.userId = p.getUserId().getId();
                this.username = p.getUserId().getUsername();
                this.fullName = p.getUserId().getFullName();
                this.email = p.getUserId().getEmail();
                this.phone = p.getUserId().getPhone();
                this.createdAt = p.getUserId().getCreatedAt();
            }
        }
    }

    public Long getProviderId() { return providerId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCompanyName() { return companyName; }
    public String getTaxCode() { return taxCode; }
    public String getHotline() { return hotline; }
    public String getBusinessAddress() { return businessAddress; }
    public Boolean getIsApproved() { return isApproved; }
    public Date getCreatedAt() { return createdAt; }

}
