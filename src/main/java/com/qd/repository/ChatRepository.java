/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.ChatRooms;
import com.qd.pojo.Users;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface ChatRepository {
    Long getOrCreateChatRoom(Users me, Users partner);
    List<ChatRooms> getMyChatRooms(Users me);
}
