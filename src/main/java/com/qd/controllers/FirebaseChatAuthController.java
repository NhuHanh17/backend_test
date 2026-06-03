package com.qd.controllers;

import java.security.Principal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.qd.pojo.ChatRooms;
import com.qd.pojo.Users;
import com.qd.repository.UserRepository;
import com.qd.repository.impl.ChatRepositoryImpl;
import com.qd.service.ChatService;
import com.qd.service.UserService;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/chat")
public class FirebaseChatAuthController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/tokens")
    public ResponseEntity<Map<String, Object>> getFirebaseCustomToken(Principal principal) {
        try {
            String username = principal.getName();
            String firebaseCustomToken = FirebaseAuth.getInstance().createCustomToken(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("firebaseToken", firebaseCustomToken);
            response.put("message", "Cấp vé thông quan Firebase an toàn thành công!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errResponse = new HashMap<>();
            errResponse.put("success", false);
            errResponse.put("message", "Sự cố mạng bảo an Firebase: " + e.getMessage());
            return ResponseEntity.status(500).body(errResponse);
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<Map<String, Object>> openChatRoom(
            Principal principal, @RequestBody Map<String, Object> body) {
        
        if (body == null || !body.containsKey("providerName")) {
            throw new RuntimeException("Thiếu tham số providerName trong Request!");
        }

        String providerName = body.get("providerName").toString();
        Users current = userService.findByUsername(principal.getName());
        Users partner = userService.findUserByProviderCompanyName(providerName);

        if (partner == null) 
            throw new RuntimeException("Không tìm thấy Nhà cung cấp nào mang tên '" + providerName + "' trên hệ thống!");
        
        if (current.getId().equals(partner.getId())) 
            throw new RuntimeException("Bạn không thể tự mở phòng chat với chính công ty của mình!");

        Long roomId = chatService.getOrCreateChatRoom(current, partner);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "roomId", roomId, 
            "message", "Đã khởi tạo phòng chat với " + providerName
        ));
    }

    @GetMapping("/rooms")
    public ResponseEntity<Map<String, Object>> getMyChatHistoryList(Principal principal) {
        Users me = userService.findByUsername(principal.getName());
        List<ChatRooms> roomsList = chatService.getMyChatRooms(me); 
        
        List<Map<String, Object>> data = roomsList.stream().map(room -> {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", room.getId());
            
            Users partner = room.getUserId().getId().equals(me.getId()) ? room.getProviderId() : room.getUserId();
            
            map.put("partnerId", partner.getId());
            map.put("partnerName", partner.getFullName());
            map.put("partnerAvatar", partner.getAvatarUrl() != null ? partner.getAvatarUrl() : "");
            map.put("createdAt", room.getCreatedAt() != null ? room.getCreatedAt().getTime() : null);
            
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
