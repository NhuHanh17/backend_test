/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.compare;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;

/**
 *
 * @author ADMIN
 */
@Component("TOUR_COMPARE_STRATEGY")
public class TourCompare implements CompareServicesStrategy{
    @Override
    public Map<String, Object> extractSpecifications(Services service) {
        Map<String, Object> specs = new HashMap<>();
        TourDetails tour = service.getTourDetails();
        if (tour != null) {
            specs.put("Thời lượng hành trình", tour.getDurationDays() + " Ngày " + tour.getDurationNights() + " Đêm");
            specs.put("Điểm khởi hành", tour.getDepartureLocation());
            specs.put("Điểm đến chính", tour.getDestinationLocation());
            specs.put("Phương tiện di chuyển", tour.getTransportMode());
        }
        return specs;
    }
}
