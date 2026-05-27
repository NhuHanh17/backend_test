/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import com.qd.enums.ItemStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;




/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "sellable_items")
@NamedQueries({
    @NamedQuery(name = "SellableItems.findAll", query = "SELECT s FROM SellableItems s"),
    @NamedQuery(name = "SellableItems.findById", query = "SELECT s FROM SellableItems s WHERE s.id = :id"),
    @NamedQuery(name = "SellableItems.findByPrice", query = "SELECT s FROM SellableItems s WHERE s.price = :price"),
    @NamedQuery(name = "SellableItems.findByAvailableSlots", query = "SELECT s FROM SellableItems s WHERE s.availableSlots = :availableSlots"),
    @NamedQuery(name = "SellableItems.findByItemStatus", query = "SELECT s FROM SellableItems s WHERE s.itemStatus = :itemStatus"),
    @NamedQuery(name = "SellableItems.findByCreatedAt", query = "SELECT s FROM SellableItems s WHERE s.createdAt = :createdAt")})
public class SellableItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "available_slots")
    private int availableSlots;
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status")
    private ItemStatus itemStatus = ItemStatus.AVAILABLE;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "itemId")
    private Set<CartItems> cartItemsSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemId")
    private Set<OrderDetails> orderDetailsSet;
    @JoinColumn(name = "hotel_room_item_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private HotelRoomItems hotelRoomItemId;
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Services serviceId;
    @JoinColumn(name = "tour_item_conc_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private TourItemConcs tourItemConcId;
    @JoinColumn(name = "transport_ticket_item_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private TransportTicketItems transportTicketItemId;

    public SellableItems() {
    }

    public SellableItems(Long id) {
        this.id = id;
    }

    public SellableItems(Long id, BigDecimal price, int availableSlots) {
        this.id = id;
        this.price = price;
        this.availableSlots = availableSlots;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public ItemStatus getItemStatus() {
        return this.itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<CartItems> getCartItemsSet() {
        return cartItemsSet;
    }

    public void setCartItemsSet(Set<CartItems> cartItemsSet) {
        this.cartItemsSet = cartItemsSet;
    }

    public Set<OrderDetails> getOrderDetailsSet() {
        return orderDetailsSet;
    }

    public void setOrderDetailsSet(Set<OrderDetails> orderDetailsSet) {
        this.orderDetailsSet = orderDetailsSet;
    }

    public HotelRoomItems getHotelRoomItemId() {
        return hotelRoomItemId;
    }

    public void setHotelRoomItemId(HotelRoomItems hotelRoomItemId) {
        this.hotelRoomItemId = hotelRoomItemId;
    }

    public Services getServiceId() {
        return serviceId;
    }

    public void setServiceId(Services serviceId) {
        this.serviceId = serviceId;
    }

    public TourItemConcs getTourItemConcId() {
        return tourItemConcId;
    }

    public void setTourItemConcId(TourItemConcs tourItemConcId) {
        this.tourItemConcId = tourItemConcId;
    }

    public TransportTicketItems getTransportTicketItemId() {
        return transportTicketItemId;
    }

    public void setTransportTicketItemId(TransportTicketItems transportTicketItemId) {
        this.transportTicketItemId = transportTicketItemId;
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
        if (!(object instanceof SellableItems)) {
            return false;
        }
        SellableItems other = (SellableItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.SellableItems[ id=" + id + " ]";
    }
    
}
