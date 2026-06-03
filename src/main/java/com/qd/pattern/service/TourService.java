/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.service;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;
import com.qd.pojo.TourItemConcs;
import com.qd.repository.ProviderRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component("TOUR_STRATEGY")
public class TourService implements ComprehensiveServiceStrategy{
    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public void saveDetails(Services service, BaseComprehensiveRequest req) {
        TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;
        TourDetails tour = new TourDetails();
        tour.setServiceId(service.getId());
        tour.setDepartureLocation(tourReq.getDepartureLocation());
        tour.setDestinationLocation(tourReq.getDestinationLocation());
        tour.setDurationDays(tourReq.getDurationDays());
        tour.setDurationNights(tourReq.getDurationNights());
        tour.setTransportMode(tourReq.getTransportMode());
        providerRepository.saveTourDetails(tour);

        if (tourReq.getTourSchedules() != null) {
            for (TourComprehensiveRequest.ScheduleInnerDTO sDto : tourReq.getTourSchedules()) {
                TourItemConcs schedule = new TourItemConcs();
                schedule.setTourDetailId(tour);
                schedule.setDepartureTime(sDto.getDepartureTime());
                schedule.setReturnTime(sDto.getReturnTime());
                schedule.setMaxParticipants(sDto.getMaxParticipants());
                providerRepository.saveTourSchedule(schedule);

                ItemStatus itemStatus = ServiceStatus.ACTIVATE.equals(service.getStatus())
                        ? (sDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK)
                        : ItemStatus.SUSPENDED;

                SellableItems sellItem = createSellable(service, sDto.getPrice(), sDto.getAvailableSlots(), itemStatus);
                sellItem.setTourItemConcId(schedule);
                providerRepository.saveSellableItem(sellItem);
            }
        }
    }

    @Override
    public void updateDetails(Services service, BaseComprehensiveRequest req) {
TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;
        TourDetails tour = service.getTourDetails();
        if (tour != null) {
            tour.setDepartureLocation(tourReq.getDepartureLocation());
            tour.setDestinationLocation(tourReq.getDestinationLocation());
            tour.setDurationDays(tourReq.getDurationDays());
            tour.setDurationNights(tourReq.getDurationNights());
            tour.setTransportMode(tourReq.getTransportMode());
            providerRepository.saveTourDetails(tour);
        }

        if (tourReq.getTourSchedules() != null && !tourReq.getTourSchedules().isEmpty()) {
            TourComprehensiveRequest.ScheduleInnerDTO sDto = tourReq.getTourSchedules().get(0);
            TourItemConcs schedule = null;
            if (tour != null && tour.getTourItemConcsSet() != null && !tour.getTourItemConcsSet().isEmpty()) {
                schedule = tour.getTourItemConcsSet().iterator().next();
            } else {
                schedule = new TourItemConcs();
                schedule.setTourDetailId(tour);
            }
            
            schedule.setDepartureTime(sDto.getDepartureTime());
            schedule.setReturnTime(sDto.getReturnTime());
            schedule.setMaxParticipants(sDto.getMaxParticipants());
            providerRepository.saveTourSchedule(schedule);

            if (service.getSellableItemsSet() == null || service.getSellableItemsSet().isEmpty()) {
                SellableItems newSellItem = new SellableItems();
                newSellItem.setServiceId(service);
                newSellItem.setTourItemConcId(schedule);
                newSellItem.setCreatedAt(new Date());
                if (service.getSellableItemsSet() == null) service.setSellableItemsSet(new HashSet<>());
                service.getSellableItemsSet().add(newSellItem);
            }
        }
    }

    @Override
    public void addSubItem(Services service, BaseComprehensiveRequest req) {
        TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;
        if (tourReq.getTourSchedules() != null && !tourReq.getTourSchedules().isEmpty()) {
            TourComprehensiveRequest.ScheduleInnerDTO sDto = tourReq.getTourSchedules().get(0);
            TourItemConcs schedule = new TourItemConcs();
            schedule.setTourDetailId(service.getTourDetails());
            schedule.setDepartureTime(sDto.getDepartureTime());
            schedule.setReturnTime(sDto.getReturnTime());
            schedule.setMaxParticipants(sDto.getMaxParticipants());
            providerRepository.saveTourSchedule(schedule);

            ItemStatus targetStatus = ServiceStatus.ACTIVATE.equals(service.getStatus())
                    ? (sDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK)
                    : ItemStatus.SUSPENDED;

            SellableItems sellItem = createSellable(service, sDto.getPrice(), sDto.getAvailableSlots(), targetStatus);
            sellItem.setTourItemConcId(schedule);
            providerRepository.saveSellableItem(sellItem);
        }
    }

    @Override
    public void updateSubItem(Services service, Long subItemId, UpdateSubItemRequest req) {
        TourItemConcs schedule = providerRepository.getTourScheduleById(subItemId);
        if (schedule == null) throw new RuntimeException("Schedule không tồn tại!");
        if (!schedule.getTourDetailId().getServiceId().equals(service.getId())) throw new RuntimeException("Vi phạm bảo mật!");

        if (req.getDepartureTime() != null) schedule.setDepartureTime(req.getDepartureTime());
        if (req.getReturnTime() != null) schedule.setReturnTime(req.getReturnTime());
        if (req.getMaxParticipants() != null) schedule.setMaxParticipants(req.getMaxParticipants());
        providerRepository.saveTourSchedule(schedule);

        SellableItems item = schedule.getSellableItems();
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
