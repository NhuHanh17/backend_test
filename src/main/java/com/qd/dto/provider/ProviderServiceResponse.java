/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import com.qd.pojo.Services;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class ProviderServiceResponse {
    private Long id;
    private String name;
    private String serviceType;
    private String status;
    private Date createdAt;

    private List<ImageDto> images;

    public ProviderServiceResponse(Services s) {
        this.id = s.getId();
        this.name = s.getName();
        this.serviceType = s.getServiceType() != null ? s.getServiceType().name() : null;
        this.status = s.getStatus() != null ? s.getStatus().name() : null;
        this.createdAt = s.getCreatedAt();

        if (s.getServiceImagesSet() != null) {
            this.images = s.getServiceImagesSet().stream()
                    .map(img -> new ImageDto(img.getId(), img.getImageUrl(), img.getIsThumbnail()))
                    .collect(Collectors.toList());
        }
    }

    public static class ImageDto {
        private Long id; 
        private String imageUrl;
        private Boolean isThumbnail;

        public ImageDto(Long id, String imageUrl, Boolean isThumbnail) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.isThumbnail = isThumbnail;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Boolean getIsThumbnail() { return isThumbnail; }
        public void setIsThumbnail(Boolean isThumbnail) { this.isThumbnail = isThumbnail; }
        
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
}
}

