/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.qd.pojo.Favorites;
import com.qd.pojo.FavoritesPK;
import com.qd.pojo.Services;
import com.qd.repository.FavoriteRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class FavoriteRepositoryImpl implements FavoriteRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public Services findServiceById(Long serviceId) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Services.class, serviceId);
    }
    
    @Override
    public Favorites findFavorite(long userId, long serviceId) {
        Session session = this.factory.getObject().getCurrentSession();
        FavoritesPK pk = new FavoritesPK(userId, serviceId);
        return session.get(Favorites.class, pk);
    }

    @Override
    public void saveFavorite(Favorites favorite) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(favorite);
    }

    @Override
    public void deleteFavorite(Favorites favorite) {
        Session session = this.factory.getObject().getCurrentSession();
        session.remove(favorite);
    }

    @Override
    public List<Favorites> getFavoriteServicesByUserId(long userId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Favorites> q = b.createQuery(Favorites.class);
        Root<Favorites> root = q.from(Favorites.class);
        Fetch<Favorites, Services> serviceFetch = root.fetch("services", JoinType.INNER);
        serviceFetch.fetch("providerId", JoinType.INNER); 

        q.select(root).where(b.equal(root.get("favoritesPK").get("userId"), userId));
        q.orderBy(b.desc(root.get("createdAt")));
        Query<Favorites> query = session.createQuery(q);

        String pageSizeStr = this.env.getProperty("services.page_size", "20"); 
        int pageSize = Integer.parseInt(pageSizeStr);
        int page = 1; 
        
        if (params != null && params.containsKey("page")) {
            try {
                page = Integer.parseInt(params.get("page"));
            } catch (NumberFormatException e) {
                page = 1; 
            }
        }
        int start = (page - 1) * pageSize;
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}
