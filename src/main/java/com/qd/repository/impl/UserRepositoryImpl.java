/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.repository.UserRepository;
import com.qd.pojo.Categories;
import com.qd.pojo.Roles;
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
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    private Session getSession() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public Users findByUsername(String username) {
//        Session session = this.factory.getObject().getCurrentSession();    
//        CriteriaBuilder b = session.getCriteriaBuilder();      
//        CriteriaQuery<Users> q = b.createQuery(Users.class);
//        Root<Users> root = q.from(Users.class);
//        q.select(root).where(b.equal(root.get("username"), username));
//        return  session.createQuery(q).uniqueResult();
        return getSession()
                .createNamedQuery("Users.findByUsername", Users.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    @Override
    public boolean isExistByUsername(String username) {
        return findByUsername(username) != null; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isExistByEmail(String email) {
        Users user = getSession()
                .createNamedQuery("Users.findByEmail", Users.class)
                .setParameter("email", email)
                .uniqueResult();
        return user != null;
    }

    @Override
    public void save(Users user) {
        getSession().merge(user);
    }

    @Override
    public void saveRegister(Users user) {
        Session session = getSession(); 
        session.persist(user); 
        session.flush(); 
    }

    @Override
    public Roles findRoleById(long roleId) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Roles.class, roleId);
    }

    @Override
    public boolean isExistByPhone(String phone) {
        Users user = getSession()
                .createNamedQuery("Users.findByPhone", Users.class)
                .setParameter("phone", phone)
                .uniqueResult();
        return user != null;
    }

    @Override
    public boolean isEmailExistForOthers(String email, Long currentUserId) {
        Users user = getSession()
                .createNamedQuery("Users.findByEmail", Users.class)
                .setParameter("email", email)
                .uniqueResult();
        return user != null && !user.getId().equals(currentUserId);
    }

    @Override
    public boolean isPhoneExistForOthers(String phone, Long currentUserId) {
        Users user = getSession()
                .createNamedQuery("Users.findByPhone", Users.class)
                .setParameter("phone", phone)
                .uniqueResult();
        return user != null && !user.getId().equals(currentUserId);
    }
}
