/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "favorites")
@NamedQueries({
    @NamedQuery(name = "Favorites.findAll", query = "SELECT f FROM Favorites f"),
    @NamedQuery(name = "Favorites.findByUserId", query = "SELECT f FROM Favorites f WHERE f.favoritesPK.userId = :userId"),
    @NamedQuery(name = "Favorites.findByServiceId", query = "SELECT f FROM Favorites f WHERE f.favoritesPK.serviceId = :serviceId"),
    @NamedQuery(name = "Favorites.findByCreatedAt", query = "SELECT f FROM Favorites f WHERE f.createdAt = :createdAt")})
public class Favorites implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FavoritesPK favoritesPK;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Services services;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Users users;

    public Favorites() {
    }

    public Favorites(FavoritesPK favoritesPK) {
        this.favoritesPK = favoritesPK;
    }

    public Favorites(long userId, long serviceId) {
        this.favoritesPK = new FavoritesPK(userId, serviceId);
    }

    public FavoritesPK getFavoritesPK() {
        return favoritesPK;
    }

    public void setFavoritesPK(FavoritesPK favoritesPK) {
        this.favoritesPK = favoritesPK;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (favoritesPK != null ? favoritesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Favorites)) {
            return false;
        }
        Favorites other = (Favorites) object;
        if ((this.favoritesPK == null && other.favoritesPK != null) || (this.favoritesPK != null && !this.favoritesPK.equals(other.favoritesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.Favorites[ favoritesPK=" + favoritesPK + " ]";
    }
    
}
