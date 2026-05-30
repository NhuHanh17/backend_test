/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 *
 * @author ADMIN
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "serviceType", // Tên trường quyết định trong JSON gửi từ Frontend 
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TourComprehensiveRequest.class, name = "TOUR"),
    @JsonSubTypes.Type(value = HotelComprehensiveRequest.class, name = "HOTEL"),
    @JsonSubTypes.Type(value = TransportComprehensiveRequest.class, name = "TRANSPORT")
})
public class BaseComprehensiveRequest {
    private String name;
    private String description;
    private String serviceType; 
    private String action;
    private List<Long> categoryIds;

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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return the categoryIds
     */
    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    /**
     * @param categoryIds the categoryIds to set
     */
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
    

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }
}
