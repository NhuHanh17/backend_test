/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.Providers;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;
import com.qd.pojo.TourItemConcs;
import com.qd.pojo.TransportDetails;
import com.qd.pojo.TransportTicketItems;

import java.util.List;
import java.util.Map;


/**
 *
 * @author ADMIN
 */
public interface ProviderRepository {
    boolean isExistsByCompanyName(String companyName);
    boolean isExistsByTaxCode(String taxCode);
    boolean isExistsByHotline(String hotline);
    List<Providers>getProviders(Map<String,String>params);
    List<Providers> getProvidersByStatus(boolean isApproved, Map<String, String> params);
    void save(Providers provider);
    Long countProvidersByStatus(boolean isApproved,Map<String, String> params);
    Providers getProviderWithUserById(Long id);
    void updateProvider(Providers provider);
    List<Services> getProviderServicesList(Long providerId, Map<String, String> params);
    Long countProviderServices(Long providerId,Map<String, String> params);

    Services getServiceById(Long id);
    Services getServiceDetailByIdAndType(Long serviceId, ServiceType type);
    void updateService(Services s);
    void updateAllSellableStatusByService(Long serviceId, ItemStatus status);
    SellableItems getSellableItemBySubItemId(Long serviceId, String targetField, Long subItemId);
    
    void saveService(Services service);
    void saveTourDetails(TourDetails tourDetails);
    void saveHotelDetails(HotelDetails hotelDetails);
    void saveTransportDetails(TransportDetails transportDetails);
    void saveTourSchedule(TourItemConcs schedule);
    void saveHotelRoomItem(HotelRoomItems roomItem);
    void saveTransportTicketItem(TransportTicketItems ticketItem);
    void saveSellableItem(SellableItems sellItem);
    Categories getCategoryById(Long id);
    void saveServiceImage(ServiceImages img);

    void updateSingleSellableStatus(Long serviceId, String targetField, Long subItemId, ItemStatus status);
    void removeServiceImage(ServiceImages img);

    HotelRoomItems getRoomById(Long id);
    TourItemConcs getTourScheduleById(Long id);
    TransportTicketItems getTransportTicketById(Long id);
    SellableItems getSellableItemById(Long id);
}
