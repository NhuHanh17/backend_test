package com.qd.dto.provider;

import java.math.BigDecimal;
import java.util.Date;

public class UpdateSubItemRequest {

    // tour/ transport
    private Date departureTime;
    private Date returnTime;
    private Date arrivalTime;

    private Integer maxParticipants;

    private Long durationMinutes;
    private String seatClass;

    private String roomType;
    private Integer capacity;
    private String bedType;
    private Integer roomSizeM2;
    private String roomAmenities;

    private BigDecimal price;
    private Integer availableSlots;

    /**
     * @return the departureTime
     */
    public Date getDepartureTime() {
        return departureTime;
    }

    /**
     * @param departureTime the departureTime to set
     */
    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * @return the returnTime
     */
    public Date getReturnTime() {
        return returnTime;
    }

    /**
     * @param returnTime the returnTime to set
     */
    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    /**
     * @return the arrivalTime
     */
    public Date getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @param arrivalTime the arrivalTime to set
     */
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * @return the maxParticipants
     */
    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * @param maxParticipants the maxParticipants to set
     */
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * @return the durationMinutes
     */
    public Long getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * @param durationMinutes the durationMinutes to set
     */
    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /**
     * @return the seatClass
     */
    public String getSeatClass() {
        return seatClass;
    }

    /**
     * @param seatClass the seatClass to set
     */
    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    /**
     * @return the roomType
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    /**
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the bedType
     */
    public String getBedType() {
        return bedType;
    }

    /**
     * @param bedType the bedType to set
     */
    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    /**
     * @return the roomSizeM2
     */
    public Integer getRoomSizeM2() {
        return roomSizeM2;
    }

    /**
     * @param roomSizeM2 the roomSizeM2 to set
     */
    public void setRoomSizeM2(Integer roomSizeM2) {
        this.roomSizeM2 = roomSizeM2;
    }

    /**
     * @return the roomAmenities
     */
    public String getRoomAmenities() {
        return roomAmenities;
    }

    /**
     * @param roomAmenities the roomAmenities to set
     */
    public void setRoomAmenities(String roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the availableSlots
     */
    public Integer getAvailableSlots() {
        return availableSlots;
    }

    /**
     * @param availableSlots the availableSlots to set
     */
    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }

    
}