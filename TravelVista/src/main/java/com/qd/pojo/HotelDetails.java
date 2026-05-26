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
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "hotel_details")
@NamedQueries({
    @NamedQuery(name = "HotelDetails.findAll", query = "SELECT h FROM HotelDetails h"),
    @NamedQuery(name = "HotelDetails.findByServiceId", query = "SELECT h FROM HotelDetails h WHERE h.serviceId = :serviceId"),
    @NamedQuery(name = "HotelDetails.findByStarRating", query = "SELECT h FROM HotelDetails h WHERE h.starRating = :starRating"),
    @NamedQuery(name = "HotelDetails.findByAddress", query = "SELECT h FROM HotelDetails h WHERE h.address = :address"),
    @NamedQuery(name = "HotelDetails.findByCity", query = "SELECT h FROM HotelDetails h WHERE h.city = :city"),
    @NamedQuery(name = "HotelDetails.findByCheckinTime", query = "SELECT h FROM HotelDetails h WHERE h.checkinTime = :checkinTime"),
    @NamedQuery(name = "HotelDetails.findByCheckoutTime", query = "SELECT h FROM HotelDetails h WHERE h.checkoutTime = :checkoutTime")})
public class HotelDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private Long serviceId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "star_rating")
    private int starRating;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "city")
    private String city;
    @Column(name = "checkin_time")
    @Temporal(TemporalType.TIME)
    private Date checkinTime;
    @Column(name = "checkout_time")
    @Temporal(TemporalType.TIME)
    private Date checkoutTime;
    @Lob
    @Size(max = 65535)
    @Column(name = "amenities")
    private String amenities;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelDetailId")
    private Set<HotelRoomItems> hotelRoomItemsSet;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Services services;

    public HotelDetails() {
    }

    public HotelDetails(Long serviceId) {
        this.serviceId = serviceId;
    }

    public HotelDetails(Long serviceId, int starRating, String address, String city) {
        this.serviceId = serviceId;
        this.starRating = starRating;
        this.address = address;
        this.city = city;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public Date getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(Date checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public Set<HotelRoomItems> getHotelRoomItemsSet() {
        return hotelRoomItemsSet;
    }

    public void setHotelRoomItemsSet(Set<HotelRoomItems> hotelRoomItemsSet) {
        this.hotelRoomItemsSet = hotelRoomItemsSet;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
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
        if (!(object instanceof HotelDetails)) {
            return false;
        }
        HotelDetails other = (HotelDetails) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.HotelDetails[ serviceId=" + serviceId + " ]";
    }
    
}
