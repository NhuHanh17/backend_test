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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
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
@Table(name = "transport_ticket_items")
@NamedQueries({
    @NamedQuery(name = "TransportTicketItems.findAll", query = "SELECT t FROM TransportTicketItems t"),
    @NamedQuery(name = "TransportTicketItems.findById", query = "SELECT t FROM TransportTicketItems t WHERE t.id = :id"),
    @NamedQuery(name = "TransportTicketItems.findByDepartureTime", query = "SELECT t FROM TransportTicketItems t WHERE t.departureTime = :departureTime"),
    @NamedQuery(name = "TransportTicketItems.findByArrivalTime", query = "SELECT t FROM TransportTicketItems t WHERE t.arrivalTime = :arrivalTime"),
    @NamedQuery(name = "TransportTicketItems.findByDurationMinutes", query = "SELECT t FROM TransportTicketItems t WHERE t.durationMinutes = :durationMinutes"),
    @NamedQuery(name = "TransportTicketItems.findBySeatClass", query = "SELECT t FROM TransportTicketItems t WHERE t.seatClass = :seatClass")})
public class TransportTicketItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "departure_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date departureTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "arrival_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_minutes")
    private long durationMinutes;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "seat_class")
    private String seatClass;
    @OneToOne(mappedBy = "transportTicketItemId")
    private SellableItems sellableItems;
    @JoinColumn(name = "transport_detail_id", referencedColumnName = "service_id")
    @ManyToOne(optional = false)
    private TransportDetails transportDetailId;

    public TransportTicketItems() {
    }

    public TransportTicketItems(Long id) {
        this.id = id;
    }

    public TransportTicketItems(Long id, Date departureTime, Date arrivalTime, long durationMinutes, String seatClass) {
        this.id = id;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationMinutes = durationMinutes;
        this.seatClass = seatClass;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public SellableItems getSellableItems() {
        return sellableItems;
    }

    public void setSellableItems(SellableItems sellableItems) {
        this.sellableItems = sellableItems;
    }

    public TransportDetails getTransportDetailId() {
        return transportDetailId;
    }

    public void setTransportDetailId(TransportDetails transportDetailId) {
        this.transportDetailId = transportDetailId;
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
        if (!(object instanceof TransportTicketItems)) {
            return false;
        }
        TransportTicketItems other = (TransportTicketItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.TransportTicketItems[ id=" + id + " ]";
    }
    
}
