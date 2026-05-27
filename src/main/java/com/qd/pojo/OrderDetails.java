/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import com.qd.enums.BookingStatus;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "order_details")
@NamedQueries({
    @NamedQuery(name = "OrderDetails.findAll", query = "SELECT o FROM OrderDetails o"),
    @NamedQuery(name = "OrderDetails.findById", query = "SELECT o FROM OrderDetails o WHERE o.id = :id"),
    @NamedQuery(name = "OrderDetails.findByQuantity", query = "SELECT o FROM OrderDetails o WHERE o.quantity = :quantity"),
    @NamedQuery(name = "OrderDetails.findByPrice", query = "SELECT o FROM OrderDetails o WHERE o.price = :price"),
    @NamedQuery(name = "OrderDetails.findByBookingStatus", query = "SELECT o FROM OrderDetails o WHERE o.bookingStatus = :bookingStatus"),
    @NamedQuery(name = "OrderDetails.findByCreatedAt", query = "SELECT o FROM OrderDetails o WHERE o.createdAt = :createdAt"),
    @NamedQuery(name = "OrderDetails.findByServiceNameSnapshot", query = "SELECT o FROM OrderDetails o WHERE o.serviceNameSnapshot = :serviceNameSnapshot"),
    @NamedQuery(name = "OrderDetails.findByProviderNameSnapshot", query = "SELECT o FROM OrderDetails o WHERE o.providerNameSnapshot = :providerNameSnapshot")})
public class OrderDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus bookingStatus = BookingStatus.BOOKED;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "service_name_snapshot")
    private String serviceNameSnapshot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "provider_name_snapshot")
    private String providerNameSnapshot;
    @Lob
    @Size(max = 65535)
    @Column(name = "item_description_snapshot")
    private String itemDescriptionSnapshot;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orders orderId;
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SellableItems itemId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "orderDetailId",fetch = FetchType.LAZY)
    private Reviews reviews;

    public OrderDetails() {
    }

    public OrderDetails(Long id) {
        this.id = id;
    }

    public OrderDetails(Long id, int quantity, BigDecimal price, String serviceNameSnapshot, String providerNameSnapshot) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.serviceNameSnapshot = serviceNameSnapshot;
        this.providerNameSnapshot = providerNameSnapshot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getServiceNameSnapshot() {
        return serviceNameSnapshot;
    }

    public void setServiceNameSnapshot(String serviceNameSnapshot) {
        this.serviceNameSnapshot = serviceNameSnapshot;
    }

    public String getProviderNameSnapshot() {
        return providerNameSnapshot;
    }

    public void setProviderNameSnapshot(String providerNameSnapshot) {
        this.providerNameSnapshot = providerNameSnapshot;
    }

    public String getItemDescriptionSnapshot() {
        return itemDescriptionSnapshot;
    }

    public void setItemDescriptionSnapshot(String itemDescriptionSnapshot) {
        this.itemDescriptionSnapshot = itemDescriptionSnapshot;
    }

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public SellableItems getItemId() {
        return itemId;
    }

    public void setItemId(SellableItems itemId) {
        this.itemId = itemId;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
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
        if (!(object instanceof OrderDetails)) {
            return false;
        }
        OrderDetails other = (OrderDetails) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.OrderDetails[ id=" + id + " ]";
    }

    /**
     * @return the bookingStatus
     */
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    /**
     * @param bookingStatus the bookingStatus to set
     */
    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
}
