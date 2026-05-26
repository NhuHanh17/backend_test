/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "tour_details")
@NamedQueries({
    @NamedQuery(name = "TourDetails.findAll", query = "SELECT t FROM TourDetails t"),
    @NamedQuery(name = "TourDetails.findByServiceId", query = "SELECT t FROM TourDetails t WHERE t.serviceId = :serviceId"),
    @NamedQuery(name = "TourDetails.findByDepartureLocation", query = "SELECT t FROM TourDetails t WHERE t.departureLocation = :departureLocation"),
    @NamedQuery(name = "TourDetails.findByDestinationLocation", query = "SELECT t FROM TourDetails t WHERE t.destinationLocation = :destinationLocation"),
    @NamedQuery(name = "TourDetails.findByDurationDays", query = "SELECT t FROM TourDetails t WHERE t.durationDays = :durationDays"),
    @NamedQuery(name = "TourDetails.findByDurationNights", query = "SELECT t FROM TourDetails t WHERE t.durationNights = :durationNights"),
    @NamedQuery(name = "TourDetails.findByTransportMode", query = "SELECT t FROM TourDetails t WHERE t.transportMode = :transportMode")})
public class TourDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private Long serviceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "departure_location")
    private String departureLocation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "destination_location")
    private String destinationLocation;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_days")
    private int durationDays;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_nights")
    private int durationNights;
    @Size(max = 100)
    @Column(name = "transport_mode")
    private String transportMode;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Services services;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tourDetailId")
    private Set<TourItemConcs> tourItemConcsSet;

    public TourDetails() {
    }

    public TourDetails(Long serviceId) {
        this.serviceId = serviceId;
    }

    public TourDetails(Long serviceId, String departureLocation, String destinationLocation, int durationDays, int durationNights) {
        this.serviceId = serviceId;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.durationDays = durationDays;
        this.durationNights = durationNights;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public int getDurationNights() {
        return durationNights;
    }

    public void setDurationNights(int durationNights) {
        this.durationNights = durationNights;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Set<TourItemConcs> getTourItemConcsSet() {
        return tourItemConcsSet;
    }

    public void setTourItemConcsSet(Set<TourItemConcs> tourItemConcsSet) {
        this.tourItemConcsSet = tourItemConcsSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TourDetails)) {
            return false;
        }
        TourDetails other = (TourDetails) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.TourDetails[ serviceId=" + serviceId + " ]";
    }
    
}
