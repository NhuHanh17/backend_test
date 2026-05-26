/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.repository.UserRepository;
import com.qd.pojo.Categories;
import com.qd.pojo.Users;
import com.qd.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;
    
        @Override
        public Users findByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();      
        CriteriaQuery<Users> q = b.createQuery(Users.class);
        Root<Users> root = q.from(Users.class);
        q.select(root).where(b.equal(root.get("username"), username));
        return  session.createQuery(q).uniqueResult();
    }
    
    @Override
    public boolean isExistByUsername(String username) {
        return findByUsername(username)!=null; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isExistByEmail(String email) {
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();      
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Users> root = q.from(Users.class);
        q.select(b.count(root)).where(b.equal(root.get("email"), email));
        
        Long count =session.createQuery(q).uniqueResult();
        return count!=null &&count >0;
    }


    @Override
    public void save(Users user) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(user); //persist tao moi; merge de saveOrUpdate    }
    }
}
