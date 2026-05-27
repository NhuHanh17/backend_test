/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto;

/**
 *
 * @author ADMIN
 */
public class UserProfile {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String roleName;

    private String companyName;
    private String taxCode;
    private String hotline;
    private String businessAddress;
    private boolean isApproved;

    public UserProfile() {
    }

    public UserProfile(String username, String email, String phone, String avatar, String role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatar;
        this.roleName = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String role) {
        this.roleName = role;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the taxCode
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * @param taxCode the taxCode to set
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * @return the hotline
     */
    public String getHotline() {
        return hotline;
    }

    /**
     * @param hotline the hotline to set
     */
    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    /**
     * @return the businessAddress
     */
    public String getBusinessAddress() {
        return businessAddress;
    }

    /**
     * @param businessAddress the businessAddress to set
     */
    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    /**
     * @return the isApproved
     */
    public boolean isIsApproved() {
        return isApproved;
    }

    /**
     * @param isApproved the isApproved to set
     */
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
}
