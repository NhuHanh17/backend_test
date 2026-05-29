/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.dto.provider;

import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class ProviderHotelDetailResponse {
    private Long id;
    private String name;
    private String address;
    private List<RoomDTO> rooms;
    private List<ImageDto> images;
    

    public ProviderHotelDetailResponse(Services s) {
        this.id = s.getId();
        this.name = s.getName();
        if (s.getHotelDetails() != null) {
            this.address = s.getHotelDetails().getAddress();
            if (s.getHotelDetails().getHotelRoomItemsSet() != null) {
                this.rooms = s.getHotelDetails().getHotelRoomItemsSet().stream()
                        .map(RoomDTO::new).collect(Collectors.toList());
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

    public static class RoomDTO {
        private Long roomId;
        private String roomType;
        private int capacity;
        private String bedType;
        private Integer roomSizeM2;
        private ItemDTO sellItem;

        public RoomDTO(com.qd.pojo.HotelRoomItems room) {
            this.roomId = room.getId();
            this.roomType = room.getRoomType();
            this.capacity = room.getCapacity();
            this.bedType = room.getBedType();
            this.roomSizeM2 = room.getRoomSizeM2();
            
            if (room.getSellableItems() != null) {
                this.sellItem = new ItemDTO(room.getSellableItems());
            }
        }
       public Long getRoomId() { return roomId; }
        public String getRoomType() { return roomType; }
        public int getCapacity() { return capacity; }
        public String getBedType() { return bedType; }
        public Integer getRoomSizeM2() { return roomSizeM2; }
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
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the rooms
     */
    public List<RoomDTO> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<RoomDTO> rooms) {
        this.rooms = rooms;
    }
    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }
}
