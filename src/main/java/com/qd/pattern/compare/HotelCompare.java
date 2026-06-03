/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.compare;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.qd.pojo.HotelDetails;
import com.qd.pojo.Services;

/**
 *
 * @author ADMIN
 */
@Component("HOTEL_COMPARE_STRATEGY")
public class HotelCompare implements CompareServicesStrategy{
    
   @Override
    public Map<String, Object> extractSpecifications(Services service) {
        Map<String, Object> specs = new HashMap<>();
        HotelDetails hotel = service.getHotelDetails();
        if (hotel != null) {
            specs.put("Tiêu chuẩn sao", hotel.getStarRating() + " Sao");
            specs.put("Thành phố", hotel.getCity());
            specs.put("Địa chỉ cụ thể", hotel.getAddress());
            specs.put("Giờ nhận/trả phòng", "Checkin: " + hotel.getCheckinTime() + " | Checkout: " + hotel.getCheckoutTime());
            specs.put("Tiện ích nổi bật", hotel.getAmenities());
        }
        return specs;
    } 
}
