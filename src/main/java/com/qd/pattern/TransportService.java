/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.pojo.TransportDetails;
import com.qd.pojo.TransportTicketItems;
import com.qd.repository.ProviderRepository;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component("TRANSPORT_STRATEGY")
public class TransportService implements ComprehensiveServiceStrategy{

    @Autowired
    private ProviderRepository providerRepository;
    
    @Override
    public void saveDetails(Services service, BaseComprehensiveRequest req) {
        TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;
        TransportDetails trans = new TransportDetails();
        trans.setServiceId(service.getId());
        trans.setBrandName(transReq.getBrandName());
        trans.setVehicleType(transReq.getVehicleType());
        trans.setDepartureStation(transReq.getDepartureStation());
        trans.setArrivalStation(transReq.getArrivalStation());
        providerRepository.saveTransportDetails(trans);

        if (transReq.getTransportTickets() != null) {
            for (TransportComprehensiveRequest.TicketInnerDTO tDto : transReq.getTransportTickets()) {
                TransportTicketItems ticket = new TransportTicketItems();
                ticket.setTransportDetailId(trans);
                ticket.setDepartureTime(tDto.getDepartureTime());
                ticket.setArrivalTime(tDto.getArrivalTime());
                ticket.setDurationMinutes(tDto.getDurationMinutes());
                ticket.setSeatClass(tDto.getSeatClass());
                providerRepository.saveTransportTicketItem(ticket);

                ItemStatus itemStatus = ServiceStatus.ACTIVATE.equals(service.getStatus())
                        ? (tDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK)
                        : ItemStatus.SUSPENDED;

                SellableItems sellItem = createSellable(service, tDto.getPrice(), tDto.getAvailableSlots(), itemStatus);
                sellItem.setTransportTicketItemId(ticket);
                providerRepository.saveSellableItem(sellItem);
            }
        }
    }

    @Override
    public void updateDetails(Services service, BaseComprehensiveRequest req) {
        TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;
        TransportDetails trans = service.getTransportDetails();
        if (trans != null) {
            trans.setBrandName(transReq.getBrandName());
            trans.setVehicleType(transReq.getVehicleType());
            trans.setDepartureStation(transReq.getDepartureStation());
            trans.setArrivalStation(transReq.getArrivalStation());
            providerRepository.saveTransportDetails(trans);
        }
    }

    @Override
    public void addSubItem(Services service, BaseComprehensiveRequest req) {
        TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;
        if (transReq.getTransportTickets() != null && !transReq.getTransportTickets().isEmpty()) {
            TransportComprehensiveRequest.TicketInnerDTO tDto = transReq.getTransportTickets().get(0);
            TransportTicketItems ticket = new TransportTicketItems();
            ticket.setTransportDetailId(service.getTransportDetails());
            ticket.setDepartureTime(tDto.getDepartureTime());
            ticket.setArrivalTime(tDto.getArrivalTime());
            ticket.setDurationMinutes(tDto.getDurationMinutes());
            ticket.setSeatClass(tDto.getSeatClass());
            providerRepository.saveTransportTicketItem(ticket);

            ItemStatus targetStatus = ServiceStatus.ACTIVATE.equals(service.getStatus())
                    ? (tDto.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK)
                    : ItemStatus.SUSPENDED;

            SellableItems sellItem = createSellable(service, tDto.getPrice(), tDto.getAvailableSlots(), targetStatus);
            sellItem.setTransportTicketItemId(ticket);
            providerRepository.saveSellableItem(sellItem);
        }
    }

    @Override
    public void updateSubItem(Services service, Long subItemId, UpdateSubItemRequest req) {
        TransportTicketItems ticket = providerRepository.getTransportTicketById(subItemId);
        if (ticket == null) throw new RuntimeException("Ticket không tồn tại!");
        if (!ticket.getTransportDetailId().getServiceId().equals(service.getId())) throw new RuntimeException("Vi phạm bảo mật!");

        if (req.getDepartureTime() != null) ticket.setDepartureTime(req.getDepartureTime());
        if (req.getArrivalTime() != null) ticket.setArrivalTime(req.getArrivalTime());
        if (req.getDurationMinutes() != null) ticket.setDurationMinutes(req.getDurationMinutes());
        if (req.getSeatClass() != null) ticket.setSeatClass(req.getSeatClass());
        providerRepository.saveTransportTicketItem(ticket);

        SellableItems item = ticket.getSellableItems();
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
