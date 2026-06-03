/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.pojo.ChatRooms;
import com.qd.pojo.Users;
import com.qd.repository.ChatRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class ChatRepositoryImpl implements ChatRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    @Transactional
    public Long getOrCreateChatRoom(Users me, Users partner) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<ChatRooms> root = q.from(ChatRooms.class);
        q.select(root.get("id"));

        Predicate caseA = b.and(b.equal(root.get("userId"), me), b.equal(root.get("providerId"), partner));
        Predicate caseB = b.and(b.equal(root.get("userId"), partner), b.equal(root.get("providerId"), me));
        q.where(b.or(caseA, caseB));

        List<Long> results = session.createQuery(q).getResultList();
        if (!results.isEmpty()) {
            return results.get(0); 
        }

        ChatRooms newRoom = new ChatRooms();
        newRoom.setUserId(me);
        newRoom.setProviderId(partner);
        newRoom.setCreatedAt(new Date());

        session.persist(newRoom); 

        return newRoom.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRooms> getMyChatRooms(Users me) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<ChatRooms> q = b.createQuery(ChatRooms.class);
        Root<ChatRooms> root = q.from(ChatRooms.class);
        root.fetch("userId", JoinType.INNER);
        root.fetch("providerId", JoinType.INNER);

        Predicate isMeUser = b.equal(root.get("userId"), me);
        Predicate isMeProvider = b.equal(root.get("providerId"), me);
        
        q.select(root).where(b.or(isMeUser, isMeProvider));
        q.orderBy(b.desc(root.get("createdAt")));

        return session.createQuery(q).getResultList();
    }

}
