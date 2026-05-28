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
public class TransportComprehensiveRequest extends BaseComprehensiveRequest{
    private String brandName;
    private String vehicleType;
    private String departureStation;
    private String arrivalStation;
    private List<TicketInnerDTO> transportTickets;
    public static class TicketInnerDTO{
        private Date departureTime;
        private Date arrivalTime;
        private long durationMinutes;
        private String seatClass;
        private BigDecimal price;
        private int availableSlots;

        public Date getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(Date departureTime) {
            this.departureTime = departureTime;
        }

        public Date getArrivalTime() {
            return arrivalTime;
        }
        public void setArrivalTime(Date arrivalTime) {
            this.arrivalTime = arrivalTime;
        }
        public long getDurationMinutes() {
            return durationMinutes;
        }
        public void setDurationMinutes(long durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
        public String getSeatClass() {
            return seatClass;
        }
        public void setSeatClass(String seatClass) {
            this.seatClass = seatClass;
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
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName the brandName to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return the vehicleType
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * @param vehicleType the vehicleType to set
     */
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * @return the departureStation
     */
    public String getDepartureStation() {
        return departureStation;
    }

    /**
     * @param departureStation the departureStation to set
     */
    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    /**
     * @return the arrivalStation
     */
    public String getArrivalStation() {
        return arrivalStation;
    }

    /**
     * @param arrivalStation the arrivalStation to set
     */
    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    /**
     * @return the transportTickets
     */
    public List<TicketInnerDTO> getTransportTickets() {
        return transportTickets;
    }

    /**
     * @param transportTickets the transportTickets to set
     */
    public void setTransportTickets(List<TicketInnerDTO> transportTickets) {
        this.transportTickets = transportTickets;
    }
}
