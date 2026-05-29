/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import com.qd.pojo.Services;
import com.qd.pojo.TourItemConcs;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class ProviderTourDetailResponse {
    private Long id;
    private String name;
    private String departureLocation;
    private String destinationLocation;
    private List<ScheduleDTO> schedules;
    private List<ImageDto> images;

    public ProviderTourDetailResponse(Services s) {
        this.id = s.getId();
        this.name = s.getName();
        if (s.getTourDetails() != null) {
            this.departureLocation = s.getTourDetails().getDepartureLocation();
            this.destinationLocation = s.getTourDetails().getDestinationLocation();
            if (s.getTourDetails().getTourItemConcsSet() != null) {
                this.schedules = s.getTourDetails().getTourItemConcsSet().stream()
                        .map(ScheduleDTO::new).collect(Collectors.toList());
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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Boolean getIsThumbnail() {
            return isThumbnail;
        }

        public void setIsThumbnail(Boolean isThumbnail) {
            this.isThumbnail = isThumbnail;
        }
    }

    public static class ScheduleDTO {
        private Long scheduleId;
        private java.util.Date departureTime;
        private int maxParticipants;
        private ItemDTO item;

        public ScheduleDTO(TourItemConcs conc) {
            this.scheduleId = conc.getId();
            this.departureTime = conc.getDepartureTime();
            this.maxParticipants = conc.getMaxParticipants();
            if (conc.getSellableItems() != null) {
                this.item = new ItemDTO(conc.getSellableItems());
            }
        }

        public Long getScheduleId() {
            return scheduleId;
        }

        public java.util.Date getDepartureTime() {
            return departureTime;
        }

        public int getMaxParticipants() {
            return maxParticipants;
        }

        public ItemDTO getItem() {
            return item;
        }

    }

    public static class ItemDTO {
        private Long itemId;
        private java.math.BigDecimal price;
        private int availableSlots;

        public ItemDTO(com.qd.pojo.SellableItems item) {
            this.itemId = item.getId();
            this.price = item.getPrice();
            this.availableSlots = item.getAvailableSlots();
        }

        public Long getItemId() {
            return itemId;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public int getAvailableSlots() {
            return availableSlots;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public List<ScheduleDTO> getSchedules() {
        return schedules;
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }
    
}
