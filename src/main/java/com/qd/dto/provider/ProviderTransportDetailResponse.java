/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import com.qd.dto.provider.ProviderTourDetailResponse.ImageDto;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class ProviderTransportDetailResponse {
    private Long id;
    private String name;
    private String brandName;
    private String vehicleType;
    private String departureStation;
    private String arrivalStation;
    private List<TicketDTO> tickets; 
    private List<ImageDto> images;

    public ProviderTransportDetailResponse(Services s) {
        this.id = s.getId();
        this.name = s.getName();
        if (s.getTransportDetails() != null) {
            this.brandName = s.getTransportDetails().getBrandName();
            this.vehicleType = s.getTransportDetails().getVehicleType();
            this.departureStation = s.getTransportDetails().getDepartureStation();
            this.arrivalStation = s.getTransportDetails().getArrivalStation();
            
            if (s.getTransportDetails().getTransportTicketItemsSet() != null) {
                this.tickets = s.getTransportDetails().getTransportTicketItemsSet().stream()
                        .map(TicketDTO::new).collect(Collectors.toList());
            }
        }
        if (s.getServiceImagesSet() != null) {
            this.images = s.getServiceImagesSet().stream()
                    .map(img -> new ImageDto(img.getImageUrl(), img.getIsThumbnail()))
                    .collect(Collectors.toList());
        }
    }
    public static class ImageDto {
        private String imageUrl;
        private Boolean isThumbnail;

        public ImageDto(String imageUrl, Boolean isThumbnail) {
            this.imageUrl = imageUrl;
            this.isThumbnail = isThumbnail;
        }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Boolean getIsThumbnail() { return isThumbnail; }
        public void setIsThumbnail(Boolean isThumbnail) { this.isThumbnail = isThumbnail; }
    }

    public static class TicketDTO {
        private Long ticketId;
        private java.util.Date departureTime;
        private java.util.Date arrivalTime;
        private long durationMinutes;
        private String seatClass;
        private ItemDTO sellItem; 

        public TicketDTO(com.qd.pojo.TransportTicketItems ticket) {
            this.ticketId = ticket.getId();
            this.departureTime = ticket.getDepartureTime();
            this.arrivalTime = ticket.getArrivalTime();
            this.durationMinutes = ticket.getDurationMinutes();
            this.seatClass = ticket.getSeatClass();
            
            if (ticket.getSellableItems() != null) {
                this.sellItem = new ItemDTO(ticket.getSellableItems());
            }
        }

        public Long getTicketId() { return ticketId; }
        public java.util.Date getDepartureTime() { return departureTime; }
        public java.util.Date getArrivalTime() { return arrivalTime; }
        public long getDurationMinutes() { return durationMinutes; }
        public String getSeatClass() { return seatClass; }
        public ItemDTO getSellItem() { return sellItem; }
    }

        public static class ItemDTO {
        private Long itemId;
        private java.math.BigDecimal price;
        private int availableSlots;

        public ItemDTO(SellableItems item) {
            this.itemId = item.getId();
            this.price = item.getPrice();
            this.availableSlots = item.getAvailableSlots();
        }

        public Long getItemId() { return itemId; }
        public java.math.BigDecimal getPrice() { return price; }
        public int getAvailableSlots() { return availableSlots; }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBrandName() { return brandName; }
    public String getVehicleType() { return vehicleType; }
    public String getDepartureStation() { return departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public List<TicketDTO> getTickets() { return tickets; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public void setTickets(List<TicketDTO> tickets) { this.tickets = tickets; }
    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }
}
