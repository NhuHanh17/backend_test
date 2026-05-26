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
@Table(name = "transport_details")
@NamedQueries({
    @NamedQuery(name = "TransportDetails.findAll", query = "SELECT t FROM TransportDetails t"),
    @NamedQuery(name = "TransportDetails.findByServiceId", query = "SELECT t FROM TransportDetails t WHERE t.serviceId = :serviceId"),
    @NamedQuery(name = "TransportDetails.findByBrandName", query = "SELECT t FROM TransportDetails t WHERE t.brandName = :brandName"),
    @NamedQuery(name = "TransportDetails.findByVehicleType", query = "SELECT t FROM TransportDetails t WHERE t.vehicleType = :vehicleType"),
    @NamedQuery(name = "TransportDetails.findByDepartureStation", query = "SELECT t FROM TransportDetails t WHERE t.departureStation = :departureStation"),
    @NamedQuery(name = "TransportDetails.findByArrivalStation", query = "SELECT t FROM TransportDetails t WHERE t.arrivalStation = :arrivalStation")})
public class TransportDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private Long serviceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "brand_name")
    private String brandName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "vehicle_type")
    private String vehicleType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "departure_station")
    private String departureStation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "arrival_station")
    private String arrivalStation;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Services services;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transportDetailId")
    private Set<TransportTicketItems> transportTicketItemsSet;

    public TransportDetails() {
    }

    public TransportDetails(Long serviceId) {
        this.serviceId = serviceId;
    }

    public TransportDetails(Long serviceId, String brandName, String vehicleType, String departureStation, String arrivalStation) {
        this.serviceId = serviceId;
        this.brandName = brandName;
        this.vehicleType = vehicleType;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Set<TransportTicketItems> getTransportTicketItemsSet() {
        return transportTicketItemsSet;
    }

    public void setTransportTicketItemsSet(Set<TransportTicketItems> transportTicketItemsSet) {
        this.transportTicketItemsSet = transportTicketItemsSet;
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
        if (!(object instanceof TransportDetails)) {
            return false;
        }
        TransportDetails other = (TransportDetails) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.TransportDetails[ serviceId=" + serviceId + " ]";
    }
    
}
