/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;

import com.qd.pojo.Favorites;
import com.qd.pojo.Services;
import com.qd.pojo.Users;
import com.qd.repository.FavoriteRepository;
import com.qd.service.FavoriteService;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
public class FavoriteServiceImpl implements FavoriteService{
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public String toggleFavoriteService(Users user, Long serviceId) {
        
        Services service = favoriteRepository.findServiceById(serviceId);
        if (service == null) {
            throw new RuntimeException("Dịch vụ  này không tồn tại");
        }

        Favorites existingFav = favoriteRepository.findFavorite(user.getId(), serviceId);

        if (existingFav != null) {
            favoriteRepository.deleteFavorite(existingFav);
            return "Bỏ yêu thích";
        } else {
            Favorites newFav = new Favorites(user.getId(), serviceId);
            newFav.setUsers(user);
            newFav.setServices(service);
            newFav.setCreatedAt(new Date());

            favoriteRepository.saveFavorite(newFav);
            return "Yêu thích";
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Favorites> getMyFavorites(Users me, Map<String, String> params) {
        return favoriteRepository.getFavoriteServicesByUserId(me.getId(), params);
    }
}
