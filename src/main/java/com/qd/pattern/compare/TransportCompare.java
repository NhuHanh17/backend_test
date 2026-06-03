/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.compare;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.qd.pojo.Services;
import com.qd.pojo.TransportDetails;

/**
 *
 * @author ADMIN
 */
@Component("TRANSPORT_COMPARE_STRATEGY")
public class TransportCompare implements CompareServicesStrategy{
   @Override
    public Map<String, Object> extractSpecifications(Services service) {
        Map<String, Object> specs = new HashMap<>();
        TransportDetails trans = service.getTransportDetails();
        if (trans != null) {
            specs.put("Hãng vận chuyển", trans.getBrandName());
            specs.put("Loại phương tiện", trans.getVehicleType());
            specs.put("Bến đi", trans.getDepartureStation());
            specs.put("Bến đến", trans.getArrivalStation());
        }
        return specs;
    } 
}
