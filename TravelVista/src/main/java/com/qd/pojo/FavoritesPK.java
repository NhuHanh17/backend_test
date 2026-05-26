/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
@Embeddable
public class FavoritesPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private long userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private long serviceId;

    public FavoritesPK() {
    }

    public FavoritesPK(long userId, long serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) serviceId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FavoritesPK)) {
            return false;
        }
        FavoritesPK other = (FavoritesPK) object;
        if (this.userId != other.userId) {
            return false;
        }
        if (this.serviceId != other.serviceId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.FavoritesPK[ userId=" + userId + ", serviceId=" + serviceId + " ]";
    }
    
}
