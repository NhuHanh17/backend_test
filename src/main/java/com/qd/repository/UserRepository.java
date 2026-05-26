/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.Users;

/**
 *
 * @author ADMIN
 */
public interface UserRepository {
    Users findByUsername(String username);
    boolean isExistByUsername(String username);
    boolean isExistByEmail(String email);
    void save(Users user);
}
