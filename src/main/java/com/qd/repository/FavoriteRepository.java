/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.Favorites;
import com.qd.pojo.Services;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface FavoriteRepository {
    public Services findServiceById(Long serviceId);
    public Favorites findFavorite(long userId, long serviceId);
    public void saveFavorite(Favorites favorite);
    public void deleteFavorite(Favorites favorite);
    public List<Favorites> getFavoriteServicesByUserId(long userId, Map<String, String> params);
    boolean isServiceFavorited(long userId, long serviceId);
}
