/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.Roles;
import com.qd.pojo.Users;

/**
 *
 * @author ADMIN
 */
public interface UserRepository {
    Users findByUsername(String username);
    Roles findRoleById(long roleId);
    boolean isExistByUsername(String username);
    boolean isExistByEmail(String email);
    boolean isExistByPhone(String phone);
    void save(Users user);
    boolean isEmailExistForOthers(String email, Long currentUserId);
    boolean isPhoneExistForOthers(String phone, Long currentUserId);
    void saveRegister(Users user);
    public Users findById(Long id);
    
}
