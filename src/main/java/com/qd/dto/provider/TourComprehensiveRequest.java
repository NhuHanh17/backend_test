/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class TourComprehensiveRequest  extends BaseComprehensiveRequest{
    private String departureLocation;
    private String destinationLocation;
    private int durationDays;
    private int durationNights;
    private String transportMode;
    private List<ScheduleInnerDTO> tourSchedules;

    public static class ScheduleInnerDTO{
        private Date departureTime;
        private Date returnTime;
        private int maxParticipants;
        private BigDecimal price;
        private int availableSlots;

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

    /**
     * @return the departureLocation
     */
    public String getDepartureLocation() {
        return departureLocation;
    }

    /**
     * @param departureLocation the departureLocation to set
     */
    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    /**
     * @return the destinationLocation
     */
    public String getDestinationLocation() {
        return destinationLocation;
    }

    /**
     * @param destinationLocation the destinationLocation to set
     */
    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    /**
     * @return the durationDays
     */
    public int getDurationDays() {
        return durationDays;
    }

    /**
     * @param durationDays the durationDays to set
     */
    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    /**
     * @return the durationNights
     */
    public int getDurationNights() {
        return durationNights;
    }

    /**
     * @param durationNights the durationNights to set
     */
    public void setDurationNights(int durationNights) {
        this.durationNights = durationNights;
    }

    /**
     * @return the transportMode
     */
    public String getTransportMode() {
        return transportMode;
    }

    /**
     * @param transportMode the transportMode to set
     */
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * @return the tourSchedules
     */
    public List<ScheduleInnerDTO> getTourSchedules() {
        return tourSchedules;
    }

    /**
     * @param tourSchedules the tourSchedules to set
     */
    public void setTourSchedules(List<ScheduleInnerDTO> tourSchedules) {
        this.tourSchedules = tourSchedules;
    }
}
