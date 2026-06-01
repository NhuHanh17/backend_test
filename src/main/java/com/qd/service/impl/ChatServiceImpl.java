/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;

import com.qd.pojo.ChatRooms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;

import com.qd.pojo.Users;
import com.qd.repository.ChatRepository;
import com.qd.service.ChatService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author ADMIN
 */
@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public Long getOrCreateChatRoom(Users me, Users partner) {
        return chatRepository.getOrCreateChatRoom(me, partner);
    }

    @Override
    public List<ChatRooms> getMyChatRooms(Users me) {
        return chatRepository.getMyChatRooms(me);
    }

}
