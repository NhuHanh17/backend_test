/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.service;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.repository.ProviderRepository;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component("HOTEL_STRATEGY")
public class HotelService implements ComprehensiveServiceStrategy{

    @Autowired
    private ProviderRepository providerRepository;
    
    @Override
    public void saveDetails(Services service, BaseComprehensiveRequest req) {
        HotelComprehensiveRequest hotelReq = (HotelComprehensiveRequest) req;
        HotelDetails hotel = new HotelDetails();
        hotel.setServiceId(service.getId());
        hotel.setStarRating(hotelReq.getStarRating());
        hotel.setAddress(hotelReq.getAddress());
        hotel.setCity(hotelReq.getCity());
        if (hotelReq.getCheckinTime() != null && !hotelReq.getCheckinTime().isEmpty()) 
            hotel.setCheckinTime(java.sql.Time.valueOf(hotelReq.getCheckinTime()));
        if (hotelReq.getCheckoutTime() != null && !hotelReq.getCheckoutTime().isEmpty()) 
            hotel.setCheckoutTime(java.sql.Time.valueOf(hotelReq.getCheckoutTime()));
        hotel.setAmenities(hotelReq.getAmenities());
        providerRepository.saveHotelDetails(hotel);

        if (hotelReq.getHotelRooms() != null) {
            for (HotelComprehensiveRequest.RoomInnerDTO rDto : hotelReq.getHotelRooms()) {
                HotelRoomItems room = new HotelRoomItems();
                room.setHotelDetailId(hotel);
                room.setRoomType(rDto.getRoomType());
                room.setCapacity(rDto.getCapacity());
                room.setBedType(rDto.getBedType());
                room.setRoomSizeM2(rDto.getRoomSizeM2());
                room.setRoomAmenities(rDto.getRoomAmenities());
                providerRepository.saveHotelRoomItem(room);

                ItemStatus itemStatus = ServiceStatus.ACTIVATE.equals(service.getStatus()) 
                        ? (rDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK) 
                        : ItemStatus.SUSPENDED;
                SellableItems sellItem = createSellable(service, rDto.getPrice(), rDto.getAvailableSlots(), itemStatus);
                sellItem.setHotelRoomItemId(room);
                providerRepository.saveSellableItem(sellItem);
            }
        }
    }

    @Override
    public void updateDetails(Services service, BaseComprehensiveRequest req) {
        HotelComprehensiveRequest hotelReq = (HotelComprehensiveRequest) req;
        HotelDetails hotel = service.getHotelDetails();
        if (hotel != null) {
            hotel.setStarRating(hotelReq.getStarRating());
            hotel.setAddress(hotelReq.getAddress());
            hotel.setCity(hotelReq.getCity());
            if (hotelReq.getCheckinTime() != null && !hotelReq.getCheckinTime().isEmpty()) 
                hotel.setCheckinTime(java.sql.Time.valueOf(hotelReq.getCheckinTime()));
            if (hotelReq.getCheckoutTime() != null && !hotelReq.getCheckoutTime().isEmpty()) 
                hotel.setCheckoutTime(java.sql.Time.valueOf(hotelReq.getCheckoutTime()));
            hotel.setAmenities(hotelReq.getAmenities());
            providerRepository.saveHotelDetails(hotel);
        }
    }

    @Override
    public void addSubItem(Services service, BaseComprehensiveRequest req) {
        HotelComprehensiveRequest hotelReq = (HotelComprehensiveRequest) req;
        if (hotelReq.getHotelRooms() != null && !hotelReq.getHotelRooms().isEmpty()) {
            HotelComprehensiveRequest.RoomInnerDTO rDto = hotelReq.getHotelRooms().get(0);
            HotelRoomItems room = new HotelRoomItems();
            room.setHotelDetailId(service.getHotelDetails());
            room.setRoomType(rDto.getRoomType());
            room.setCapacity(rDto.getCapacity());
            room.setBedType(rDto.getBedType());
            room.setRoomSizeM2(rDto.getRoomSizeM2());
            room.setRoomAmenities(rDto.getRoomAmenities());
            providerRepository.saveHotelRoomItem(room);
            
            ItemStatus targetStatus = ServiceStatus.ACTIVATE.equals(service.getStatus())
                    ? (rDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK)
                    : ItemStatus.SUSPENDED;

            SellableItems sellItem = createSellable(service, rDto.getPrice(), rDto.getAvailableSlots(), targetStatus);
            sellItem.setHotelRoomItemId(room);
            providerRepository.saveSellableItem(sellItem);
        }
    }

    @Override
    public void updateSubItem(Services service, Long subItemId, UpdateSubItemRequest req) {
        HotelRoomItems room = providerRepository.getRoomById(subItemId);
        if (room == null) throw new RuntimeException("Room không tồn tại!");
        if (!room.getHotelDetailId().getServiceId().equals(service.getId())) throw new RuntimeException("Vi phạm bảo mật!");

        if (req.getRoomType() != null) room.setRoomType(req.getRoomType());
        if (req.getCapacity() != null) room.setCapacity(req.getCapacity());
        if (req.getBedType() != null) room.setBedType(req.getBedType());
        if (req.getRoomSizeM2() != null) room.setRoomSizeM2(req.getRoomSizeM2());
        if (req.getRoomAmenities() != null) room.setRoomAmenities(req.getRoomAmenities());
        providerRepository.saveHotelRoomItem(room);

        SellableItems item = room.getSellableItems();
        if (item != null) {
            if (req.getPrice() != null) item.setPrice(req.getPrice());
            if (req.getAvailableSlots() != null) {
                item.setAvailableSlots(req.getAvailableSlots());
                item.setItemStatus(req.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
            }
            providerRepository.saveSellableItem(item);
        }
    }
    
    private SellableItems createSellable(Services s, BigDecimal p, int slots, ItemStatus status) {
        SellableItems item = new SellableItems();
        item.setServiceId(s);
        item.setPrice(p);
        item.setAvailableSlots(slots);
        item.setItemStatus(status);
        item.setCreatedAt(new Date());
        return item;
    }
    
}
