package com.qd.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.ProviderHotelDetailResponse;
import com.qd.dto.provider.ProviderTourDetailResponse;
import com.qd.dto.provider.ProviderTransportDetailResponse;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;
import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;
import com.qd.pojo.TourItemConcs;
import com.qd.pojo.TransportDetails;
import com.qd.pojo.TransportTicketItems;
import com.qd.pojo.Users;
import com.qd.repository.ProviderRepository;
import com.qd.repository.UserRepository;
import com.qd.service.ProviderService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ProviderServiceImpl implements ProviderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMyServicesList(String username, Map<String, String> params) {
        Users user = userRepository.findByUsername(username);
        Long providerId = user.getProviders().getId();
        
        List<Services> servicesList = providerRepository.getProviderServicesList(providerId, params);
        Long totalElementsObj = providerRepository.countProviderServices(providerId, params);
        long totalElements = totalElementsObj != null ? totalElementsObj : 0L;

        int pageSize = this.env.getProperty("services.page_size", Integer.class, 10);
        int currentPage = (params != null) ? Integer.parseInt(params.getOrDefault("page", "1")) : 1;
        
        List<com.qd.dto.provider.ProviderServiceResponse> content = servicesList.stream()
                .map(com.qd.dto.provider.ProviderServiceResponse::new)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("content", content);
        result.put("totalElements", totalElements);
        result.put("page", currentPage);
        result.put("size", pageSize);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyServiceDetail(String username, Long id, String typeStr) {
        ServiceType type = ServiceType.valueOf(typeStr.toUpperCase());
        Services service = providerRepository.getServiceDetailByIdAndType(id, type);

        if (service == null) {
            throw new RuntimeException("Không tìm thấy bài viết dịch vụ!");
        }

        Users user = userRepository.findByUsername(username);
        if (service.getProviderId() == null || !service.getProviderId().getId().equals(user.getProviders().getId())) {
            throw new RuntimeException("Vi phạm bảo mật: Bạn không có quyền sở hữu dịch vụ này!");
        }

        switch (type) {
            case TOUR:
                return new ProviderTourDetailResponse(service);
            case HOTEL:
                return new ProviderHotelDetailResponse(service);
            case TRANSPORT:
                return new ProviderTransportDetailResponse(service);
            default:
                return service;
        }
    }

    @Autowired
    private com.cloudinary.Cloudinary cloudinary;

    @Override
    @Transactional
    public Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req,MultipartFile[] files) {
        Users user = userRepository.findByUsername(username);
        ServiceType type = ServiceType.valueOf(req.getServiceType().toUpperCase());
        
        ServiceStatus status = ServiceStatus.DRAFT;
        boolean isPublishAction = "PUBLISH".equalsIgnoreCase(req.getAction());
        if (isPublishAction) {
            status = ServiceStatus.ACTIVATE;
        }
        
        Services service = new Services();
        service.setName(req.getName());
        service.setProviderId(user.getProviders());
        service.setDescription(req.getDescription());
        service.setServiceType(type);
        service.setStatus(status);
        service.setCreatedAt(new Date());

        if (req.getCategoryId() != null) {
            Categories cat = providerRepository.getCategoryById(req.getCategoryId());
            if (cat != null) {
                Set<Categories> catSet = new HashSet<>();
                catSet.add(cat);
                service.setCategoriesSet(catSet);
            }
        }
        providerRepository.saveService(service);

        if (files != null && files.length > 0){
            boolean isFirstImage = true;
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                com.cloudinary.utils.ObjectUtils.emptyMap()
                        );
                        String secureUrl = (String) uploadResult.get("secure_url");

                        ServiceImages imgEntity = new ServiceImages();
                        imgEntity.setServiceId(service); 
                        imgEntity.setImageUrl(secureUrl); 
                        
                        if (isFirstImage) {
                            imgEntity.setIsThumbnail(true);  
                            isFirstImage = false;           
                        } else {
                            imgEntity.setIsThumbnail(false); 
                        }
                        providerRepository.saveServiceImage(imgEntity);
                        
                    } catch (java.io.IOException e) {
                        throw new RuntimeException("Thất bại: Lỗi upload các ảnh lên Cloudinary!");
                    }
                }
            }
        }


        if (req instanceof TourComprehensiveRequest) {
            TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;

            TourDetails tour = new TourDetails();
            tour.setServiceId(service.getId());
            tour.setDepartureLocation(tourReq.getDepartureLocation());
            tour.setDestinationLocation(tourReq.getDestinationLocation());
            tour.setDurationDays(tourReq.getDurationDays());
            tour.setDurationNights(tourReq.getDurationNights());
            tour.setTransportMode(tourReq.getTransportMode());
            providerRepository.saveTourDetails(tour);

            if (tourReq.getTourSchedules() != null && !tourReq.getTourSchedules().isEmpty()) {
                for (TourComprehensiveRequest.ScheduleInnerDTO sDto : tourReq.getTourSchedules()) {
                    TourItemConcs schedule = new TourItemConcs();
                    schedule.setTourDetailId(tour);
                    schedule.setDepartureTime(sDto.getDepartureTime());
                    schedule.setReturnTime(sDto.getReturnTime());
                    schedule.setMaxParticipants(sDto.getMaxParticipants());
                    providerRepository.saveTourSchedule(schedule);

                    if (isPublishAction && sDto.getAvailableSlots() > 0 && sDto.getPrice() != null && sDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        SellableItems sellItem = createUnifiedSellableItem(service, sDto.getPrice(), sDto.getAvailableSlots());
                        sellItem.setTourItemConcId(schedule);
                        providerRepository.saveSellableItem(sellItem);
                    }
                }
            }

        } else if (req instanceof HotelComprehensiveRequest) {
            HotelComprehensiveRequest hotelReq = (HotelComprehensiveRequest) req;

            HotelDetails hotel = new HotelDetails();
            hotel.setServiceId(service.getId());
            hotel.setStarRating(hotelReq.getStarRating());
            hotel.setAddress(hotelReq.getAddress());
            hotel.setCity(hotelReq.getCity());
            if (hotelReq.getCheckinTime() != null && !hotelReq.getCheckinTime().isEmpty()) {
                hotel.setCheckinTime(java.sql.Time.valueOf(hotelReq.getCheckinTime()));
            }
            if (hotelReq.getCheckoutTime() != null && !hotelReq.getCheckoutTime().isEmpty()) {
                hotel.setCheckoutTime(java.sql.Time.valueOf(hotelReq.getCheckoutTime()));
            }
            hotel.setAmenities(hotelReq.getAmenities());
            providerRepository.saveHotelDetails(hotel);

            if (hotelReq.getHotelRooms() != null && !hotelReq.getHotelRooms().isEmpty()) {
                for (HotelComprehensiveRequest.RoomInnerDTO rDto : hotelReq.getHotelRooms()) {
                    HotelRoomItems room = new HotelRoomItems();
                    room.setHotelDetailId(hotel);
                    room.setRoomType(rDto.getRoomType());
                    room.setCapacity(rDto.getCapacity());
                    room.setBedType(rDto.getBedType());
                    room.setRoomSizeM2(rDto.getRoomSizeM2());
                    room.setRoomAmenities(rDto.getRoomAmenities());
                    providerRepository.saveHotelRoomItem(room);

                    if (isPublishAction && rDto.getAvailableSlots() > 0 && rDto.getPrice() != null && rDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        SellableItems sellItem = createUnifiedSellableItem(service, rDto.getPrice(), rDto.getAvailableSlots());
                        sellItem.setHotelRoomItemId(room);
                        providerRepository.saveSellableItem(sellItem);
                    }
                }
            }

        } else if (req instanceof TransportComprehensiveRequest) {
            TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;

            TransportDetails trans = new TransportDetails();
            trans.setServiceId(service.getId());
            trans.setBrandName(transReq.getBrandName());
            trans.setVehicleType(transReq.getVehicleType());
            trans.setDepartureStation(transReq.getDepartureStation());
            trans.setArrivalStation(transReq.getArrivalStation());
            providerRepository.saveTransportDetails(trans);

            if (transReq.getTransportTickets() != null && !transReq.getTransportTickets().isEmpty()) {
                for (TransportComprehensiveRequest.TicketInnerDTO tDto : transReq.getTransportTickets()) {
                    TransportTicketItems ticket = new TransportTicketItems();
                    ticket.setTransportDetailId(trans);
                    ticket.setDepartureTime(tDto.getDepartureTime());
                    ticket.setArrivalTime(tDto.getArrivalTime());
                    ticket.setDurationMinutes(tDto.getDurationMinutes());
                    ticket.setSeatClass(tDto.getSeatClass());
                    providerRepository.saveTransportTicketItem(ticket);

                    if (isPublishAction && tDto.getAvailableSlots() > 0 && tDto.getPrice() != null && tDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        SellableItems sellItem = createUnifiedSellableItem(service, tDto.getPrice(), tDto.getAvailableSlots());
                        sellItem.setTransportTicketItemId(ticket);
                        providerRepository.saveSellableItem(sellItem);
                    }
                }
            }
        }
        return service.getId();
    }

    private SellableItems createUnifiedSellableItem(Services service, BigDecimal price, int slots) {
        SellableItems sellItem = new SellableItems();
        sellItem.setServiceId(service);
        sellItem.setPrice(price);
        sellItem.setAvailableSlots(slots);
        sellItem.setItemStatus(ItemStatus.AVAILABLE);
        sellItem.setCreatedAt(new Date());
        return sellItem;
    }
}
