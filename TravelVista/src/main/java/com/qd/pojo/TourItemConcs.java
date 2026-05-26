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
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "tour_item_concs")
@NamedQueries({
    @NamedQuery(name = "TourItemConcs.findAll", query = "SELECT t FROM TourItemConcs t"),
    @NamedQuery(name = "TourItemConcs.findById", query = "SELECT t FROM TourItemConcs t WHERE t.id = :id"),
    @NamedQuery(name = "TourItemConcs.findByDepartureTime", query = "SELECT t FROM TourItemConcs t WHERE t.departureTime = :departureTime"),
    @NamedQuery(name = "TourItemConcs.findByReturnTime", query = "SELECT t FROM TourItemConcs t WHERE t.returnTime = :returnTime"),
    @NamedQuery(name = "TourItemConcs.findByMaxParticipants", query = "SELECT t FROM TourItemConcs t WHERE t.maxParticipants = :maxParticipants")})
public class TourItemConcs implements Serializable {

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
    @Column(name = "return_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "max_participants")
    private int maxParticipants;
    @OneToOne(mappedBy = "tourItemConcId")
    private SellableItems sellableItems;
    @JoinColumn(name = "tour_detail_id", referencedColumnName = "service_id")
    @ManyToOne(optional = false)
    private TourDetails tourDetailId;

    public TourItemConcs() {
    }

    public TourItemConcs(Long id) {
        this.id = id;
    }

    public TourItemConcs(Long id, Date departureTime, Date returnTime, int maxParticipants) {
        this.id = id;
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.maxParticipants = maxParticipants;
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

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public SellableItems getSellableItems() {
        return sellableItems;
    }

    public void setSellableItems(SellableItems sellableItems) {
        this.sellableItems = sellableItems;
    }

    public TourDetails getTourDetailId() {
        return tourDetailId;
    }

    public void setTourDetailId(TourDetails tourDetailId) {
        this.tourDetailId = tourDetailId;
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
        if (!(object instanceof TourItemConcs)) {
            return false;
        }
        TourItemConcs other = (TourItemConcs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.TourItemConcs[ id=" + id + " ]";
    }
    
}
