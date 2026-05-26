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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "hotel_room_items")
@NamedQueries({
    @NamedQuery(name = "HotelRoomItems.findAll", query = "SELECT h FROM HotelRoomItems h"),
    @NamedQuery(name = "HotelRoomItems.findById", query = "SELECT h FROM HotelRoomItems h WHERE h.id = :id"),
    @NamedQuery(name = "HotelRoomItems.findByRoomType", query = "SELECT h FROM HotelRoomItems h WHERE h.roomType = :roomType"),
    @NamedQuery(name = "HotelRoomItems.findByCapacity", query = "SELECT h FROM HotelRoomItems h WHERE h.capacity = :capacity"),
    @NamedQuery(name = "HotelRoomItems.findByBedType", query = "SELECT h FROM HotelRoomItems h WHERE h.bedType = :bedType"),
    @NamedQuery(name = "HotelRoomItems.findByRoomSizeM2", query = "SELECT h FROM HotelRoomItems h WHERE h.roomSizeM2 = :roomSizeM2")})
public class HotelRoomItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "room_type")
    private String roomType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capacity")
    private int capacity;
    @Size(max = 100)
    @Column(name = "bed_type")
    private String bedType;
    @Column(name = "room_size_m2")
    private Integer roomSizeM2;
    @Lob
    @Size(max = 65535)
    @Column(name = "room_amenities")
    private String roomAmenities;
    @JoinColumn(name = "hotel_detail_id", referencedColumnName = "service_id")
    @ManyToOne(optional = false)
    private HotelDetails hotelDetailId;
    @OneToOne(mappedBy = "hotelRoomItemId")
    private SellableItems sellableItems;

    public HotelRoomItems() {
    }

    public HotelRoomItems(Long id) {
        this.id = id;
    }

    public HotelRoomItems(Long id, String roomType, int capacity) {
        this.id = id;
        this.roomType = roomType;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public Integer getRoomSizeM2() {
        return roomSizeM2;
    }

    public void setRoomSizeM2(Integer roomSizeM2) {
        this.roomSizeM2 = roomSizeM2;
    }

    public String getRoomAmenities() {
        return roomAmenities;
    }

    public void setRoomAmenities(String roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    public HotelDetails getHotelDetailId() {
        return hotelDetailId;
    }

    public void setHotelDetailId(HotelDetails hotelDetailId) {
        this.hotelDetailId = hotelDetailId;
    }

    public SellableItems getSellableItems() {
        return sellableItems;
    }

    public void setSellableItems(SellableItems sellableItems) {
        this.sellableItems = sellableItems;
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
        if (!(object instanceof HotelRoomItems)) {
            return false;
        }
        HotelRoomItems other = (HotelRoomItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.HotelRoomItems[ id=" + id + " ]";
    }
    
}
