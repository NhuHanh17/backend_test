/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.service;

import com.qd.pojo.Favorites;
import com.qd.pojo.Users;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface FavoriteService {
    public String toggleFavoriteService(Users user, Long serviceId);
    public List<Favorites> getMyFavorites(Users me, Map<String, String> params);
}
