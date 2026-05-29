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

    private List<String> images;

    public ProviderServiceResponse(Services s) {
        this.id = s.getId();
        this.name = s.getName();
        this.serviceType = s.getServiceType() != null ? s.getServiceType().name() : null;
        this.status = s.getStatus() != null ? s.getStatus().name() : null;
        this.createdAt = s.getCreatedAt();

        if (s.getServiceImagesSet() != null) {
            this.images = s.getServiceImagesSet().stream()
                    .map(img -> img.getImageUrl())
                    .collect(Collectors.toList());
        }
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
