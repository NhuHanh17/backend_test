/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "services")
@NamedQueries({
    @NamedQuery(name = "Services.findAll", query = "SELECT s FROM Services s"),
    @NamedQuery(name = "Services.findById", query = "SELECT s FROM Services s WHERE s.id = :id"),
    @NamedQuery(name = "Services.findByName", query = "SELECT s FROM Services s WHERE s.name = :name"),
    @NamedQuery(name = "Services.findByServiceType", query = "SELECT s FROM Services s WHERE s.serviceType = :serviceType"),
    @NamedQuery(name = "Services.findByStatus", query = "SELECT s FROM Services s WHERE s.status = :status"),
    @NamedQuery(name = "Services.findByBookingCount", query = "SELECT s FROM Services s WHERE s.bookingCount = :bookingCount"),
    @NamedQuery(name = "Services.findByAverageRating", query = "SELECT s FROM Services s WHERE s.averageRating = :averageRating"),
    @NamedQuery(name = "Services.findByReviewCount", query = "SELECT s FROM Services s WHERE s.reviewCount = :reviewCount"),
    @NamedQuery(name = "Services.findByCreatedAt", query = "SELECT s FROM Services s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Services.findByUpdatedAt", query = "SELECT s FROM Services s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Services.findByDeletedAt", query = "SELECT s FROM Services s WHERE s.deletedAt = :deletedAt")})
public class Services implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type",nullable = false)
    private ServiceType serviceType;    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceStatus status=ServiceStatus.DRAFT;
    @Column(name = "booking_count")
    private Integer bookingCount;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "average_rating")
    private BigDecimal averageRating;
    @Column(name = "review_count")
    private Integer reviewCount;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @ManyToMany(mappedBy = "servicesSet")
    private Set<Categories> categoriesSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "services")
    private Set<Favorites> favoritesSet;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "services")
    private TourDetails tourDetails;
    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Providers providerId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceId")
    private Set<ServiceImages> serviceImagesSet;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "services")
    private HotelDetails hotelDetails;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "services")
    private TransportDetails transportDetails;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceId")
    private Set<SellableItems> sellableItemsSet;

    public Services() {
    }

    public Services(Long id) {
        this.id = id;
    }

    public Services(Long id, String name, String description, ServiceType serviceType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
    }
    
        public Services(Long id,ServiceType serviceType, ServiceStatus status) {
        this.id = id;
        this.serviceType = serviceType;
        this.status=status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Categories> getCategoriesSet() {
        return categoriesSet;
    }

    public void setCategoriesSet(Set<Categories> categoriesSet) {
        this.categoriesSet = categoriesSet;
    }

    public Set<Favorites> getFavoritesSet() {
        return favoritesSet;
    }

    public void setFavoritesSet(Set<Favorites> favoritesSet) {
        this.favoritesSet = favoritesSet;
    }

    public TourDetails getTourDetails() {
        return tourDetails;
    }

    public void setTourDetails(TourDetails tourDetails) {
        this.tourDetails = tourDetails;
    }

    public Providers getProviderId() {
        return providerId;
    }

    public void setProviderId(Providers providerId) {
        this.providerId = providerId;
    }

    public Set<ServiceImages> getServiceImagesSet() {
        return serviceImagesSet;
    }

    public void setServiceImagesSet(Set<ServiceImages> serviceImagesSet) {
        this.serviceImagesSet = serviceImagesSet;
    }

    public HotelDetails getHotelDetails() {
        return hotelDetails;
    }

    public void setHotelDetails(HotelDetails hotelDetails) {
        this.hotelDetails = hotelDetails;
    }

    public TransportDetails getTransportDetails() {
        return transportDetails;
    }

    public void setTransportDetails(TransportDetails transportDetails) {
        this.transportDetails = transportDetails;
    }

    public Set<SellableItems> getSellableItemsSet() {
        return sellableItemsSet;
    }

    public void setSellableItemsSet(Set<SellableItems> sellableItemsSet) {
        this.sellableItemsSet = sellableItemsSet;
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
        if (!(object instanceof Services)) {
            return false;
        }
        Services other = (Services) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.Services[ id=" + id + " ]";
    }

    /**
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the status
     */
    public ServiceStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }
    
}
