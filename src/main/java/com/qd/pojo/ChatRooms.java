package com.qd.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "chat_rooms")
public class ChatRooms implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; 

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users providerId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public ChatRooms() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Users getUserId() { return userId; }
    public void setUserId(Users userId) { this.userId = userId; }

    public Users getProviderId() { return providerId; }
    public void setProviderId(Users providerId) { this.providerId = providerId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}