/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import com.qd.enums.OrderStatus;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "provider_orders")
@NamedQueries({
    @NamedQuery(name = "ProviderOrders.findAll", query = "SELECT p FROM ProviderOrders p"),
    @NamedQuery(name = "ProviderOrders.findById", query = "SELECT p FROM ProviderOrders p WHERE p.id = :id"),
    @NamedQuery(name = "ProviderOrders.findBySubtotal", query = "SELECT p FROM ProviderOrders p WHERE p.subtotal = :subtotal"),
    @NamedQuery(name = "ProviderOrders.findByOrderStatus", query = "SELECT p FROM ProviderOrders p WHERE p.orderStatus = :orderStatus"),
    @NamedQuery(name = "ProviderOrders.findByOrderMonth", query = "SELECT p FROM ProviderOrders p WHERE p.orderMonth = :orderMonth"),
    @NamedQuery(name = "ProviderOrders.findByOrderQuarter", query = "SELECT p FROM ProviderOrders p WHERE p.orderQuarter = :orderQuarter"),
    @NamedQuery(name = "ProviderOrders.findByCreatedAt", query = "SELECT p FROM ProviderOrders p WHERE p.createdAt = :createdAt")})
public class ProviderOrders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus = OrderStatus.PENDING;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_month")
    private int orderMonth;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_quarter")
    private int orderQuarter;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orders orderId;
    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Providers providerId;

    public ProviderOrders() {
    }

    public ProviderOrders(Long id) {
        this.id = id;
    }

    public ProviderOrders(Long id, BigDecimal subtotal, int orderMonth, int orderQuarter) {
        this.id = id;
        this.subtotal = subtotal;
        this.orderMonth = orderMonth;
        this.orderQuarter = orderQuarter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getOrderMonth() {
        return orderMonth;
    }

    public void setOrderMonth(int orderMonth) {
        this.orderMonth = orderMonth;
    }

    public int getOrderQuarter() {
        return orderQuarter;
    }

    public void setOrderQuarter(int orderQuarter) {
        this.orderQuarter = orderQuarter;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public Providers getProviderId() {
        return providerId;
    }

    public void setProviderId(Providers providerId) {
        this.providerId = providerId;
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
        if (!(object instanceof ProviderOrders)) {
            return false;
        }
        ProviderOrders other = (ProviderOrders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.ProviderOrders[ id=" + id + " ]";
    }

    /**
     * @return the orderStatus
     */
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    /**
     * @param orderStatus the orderStatus to set
     */
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
}
