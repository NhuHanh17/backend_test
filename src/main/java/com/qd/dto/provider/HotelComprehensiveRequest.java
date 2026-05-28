/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class HotelComprehensiveRequest  extends BaseComprehensiveRequest{
    private int starRating;
    private String address;
    private String city;
    private String checkinTime;
    private String checkoutTime;
    private String amenities;
    private List<RoomInnerDTO> hotelRooms;

    public static class RoomInnerDTO{
        private String roomType;
        private int capacity;
        private String bedType;
        private Integer roomSizeM2;
        private String roomAmenities;
        private BigDecimal price;
        private int availableSlots;

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

    public String getCheckinTime() {
        return checkinTime;
    }
    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }
    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getAmenities() {
        return amenities;
    }
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<RoomInnerDTO> getHotelRooms() {
        return hotelRooms;
    }
    public void setHotelRooms(List<RoomInnerDTO> hotelRooms) {
        this.hotelRooms = hotelRooms;
    }


}