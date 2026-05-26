/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "chat_messages")
@NamedQueries({
    @NamedQuery(name = "ChatMessages.findAll", query = "SELECT c FROM ChatMessages c"),
    @NamedQuery(name = "ChatMessages.findById", query = "SELECT c FROM ChatMessages c WHERE c.id = :id"),
    @NamedQuery(name = "ChatMessages.findByFirebaseTimestamp", query = "SELECT c FROM ChatMessages c WHERE c.firebaseTimestamp = :firebaseTimestamp"),
    @NamedQuery(name = "ChatMessages.findByIsRead", query = "SELECT c FROM ChatMessages c WHERE c.isRead = :isRead"),
    @NamedQuery(name = "ChatMessages.findByCreatedAt", query = "SELECT c FROM ChatMessages c WHERE c.createdAt = :createdAt")})
public class ChatMessages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "message_text")
    private String messageText;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "firebase_timestamp")
    private String firebaseTimestamp;
    @Column(name = "is_read")
    private Boolean isRead;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users receiverId;
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users senderId;

    public ChatMessages() {
    }

    public ChatMessages(Long id) {
        this.id = id;
    }

    public ChatMessages(Long id, String messageText, String firebaseTimestamp) {
        this.id = id;
        this.messageText = messageText;
        this.firebaseTimestamp = firebaseTimestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFirebaseTimestamp() {
        return firebaseTimestamp;
    }

    public void setFirebaseTimestamp(String firebaseTimestamp) {
        this.firebaseTimestamp = firebaseTimestamp;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Users getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Users receiverId) {
        this.receiverId = receiverId;
    }

    public Users getSenderId() {
        return senderId;
    }

    public void setSenderId(Users senderId) {
        this.senderId = senderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChatMessages)) {
            return false;
        }
        ChatMessages other = (ChatMessages) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.ChatMessages[ id=" + id + " ]";
    }
    
}
