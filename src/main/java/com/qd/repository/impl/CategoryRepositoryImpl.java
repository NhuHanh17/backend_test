/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.repository.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class CategoryRepositoryImpl implements CategoryRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;
    @Override
    public List<Categories> getCates(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Categories> q = b.createQuery(Categories.class);
        Root<Categories> root = q.from(Categories.class); 
        q.select(root); 
        
        if (params != null && params.containsKey("serviceType") && !params.get("serviceType").trim().isEmpty()) {
            try {
                ServiceType typeEnum = ServiceType.valueOf(params.get("serviceType").toUpperCase());
                q.where(b.equal(root.get("serviceType"), typeEnum)); 
            } catch (IllegalArgumentException e) {
                return List.of(); 
            }
        } 

        Query<Categories> query = session.createQuery(q);
                String pageSizeStr = this.env.getProperty("categories.page_size", "20");
        int pageSize = Integer.parseInt(pageSizeStr);
        
        int page = 1; 
        if (params != null && params.containsKey("page") && !params.get("page").trim().isEmpty()) {
            try {
                page = Integer.parseInt(params.get("page"));
                if (page < 1) page = 1; 
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


