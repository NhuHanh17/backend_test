package com.qd.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.math.BigDecimal;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.ProviderHotelDetailResponse;
import com.qd.dto.provider.ProviderServiceResponse;
import com.qd.dto.provider.ProviderTourDetailResponse;
import com.qd.dto.provider.ProviderTransportDetailResponse;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

        List<ProviderServiceResponse> content = servicesList.stream()
                .map(ProviderServiceResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
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
    private Cloudinary cloudinary;



    @Transactional
    private SellableItems createUnifiedSellableItem(Services service, BigDecimal price, int slots) {
        SellableItems sellItem = new SellableItems();
        sellItem.setServiceId(service);
        sellItem.setPrice(price);
        sellItem.setAvailableSlots(slots);
        sellItem.setItemStatus(ItemStatus.AVAILABLE);
        sellItem.setCreatedAt(new Date());
        return sellItem;
    }

    @Override
    @Transactional
    public Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req, MultipartFile[] files) {
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

        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            Set<Categories> categoriesSet = new HashSet<>();
            
            for (Long cateId : req.getCategoryIds()) {
                Categories cate = providerRepository.getCategoryById(cateId); // Gọi Repo lấy danh mục lên
                if (cate != null) {
                    if (!cate.getServiceType().equals(type)) {
                        throw new RuntimeException("Vi phạm logic dữ liệu: Danh mục " + cate.getName() 
                                + " có kiểu: " + cate.getServiceType() 
                                + " Không trùng khớp với dịch vụ bạn đang khởi tạo: " + type + "!");
                    }
                    
                    if (cate.getServicesSet() == null) {
                        cate.setServicesSet(new HashSet<>());
                    }
                    cate.getServicesSet().add(service);
                    categoriesSet.add(cate);
                }
            }
            service.setCategoriesSet(categoriesSet);
            providerRepository.saveService(service);

        }

        if (files != null && files.length > 0) {
            boolean isFirstImage = true;
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                com.cloudinary.utils.ObjectUtils.emptyMap());
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

                    // if (isPublishAction && sDto.getAvailableSlots() > 0 && sDto.getPrice() != null
                    //         && sDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //     SellableItems sellItem = createUnifiedSellableItem(service, sDto.getPrice(),
                    //             sDto.getAvailableSlots());
                    //     sellItem.setTourItemConcId(schedule);
                    //     providerRepository.saveSellableItem(sellItem);
                    // }

                    ItemStatus itemStatus = ItemStatus.SUSPENDED; 
                    if (ServiceStatus.ACTIVATE.equals(status)) {
                        itemStatus = (sDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK;
                    }

                    SellableItems sellItem = createUnifiedSellableItemCustom(service, sDto.getPrice(), sDto.getAvailableSlots(), itemStatus);
                    sellItem.setTourItemConcId(schedule);
                    providerRepository.saveSellableItem(sellItem);
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

                    // if (isPublishAction && rDto.getAvailableSlots() > 0 && rDto.getPrice() != null
                    //         && rDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //     SellableItems sellItem = createUnifiedSellableItem(service, rDto.getPrice(),
                    //             rDto.getAvailableSlots());
                    //     sellItem.setHotelRoomItemId(room);
                    //     providerRepository.saveSellableItem(sellItem);
                    // }
                    ItemStatus itemStatus = ItemStatus.SUSPENDED; 
                    if (ServiceStatus.ACTIVATE.equals(status)) {
                        itemStatus = (rDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK;
                    }

                    SellableItems sellItem = createUnifiedSellableItemCustom(service, rDto.getPrice(), rDto.getAvailableSlots(), itemStatus);
                    sellItem.setHotelRoomItemId(room);
                    providerRepository.saveSellableItem(sellItem);
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

                    // if (isPublishAction && tDto.getAvailableSlots() > 0 && tDto.getPrice() != null
                    //         && tDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //     SellableItems sellItem = createUnifiedSellableItem(service, tDto.getPrice(),
                    //             tDto.getAvailableSlots());
                    //     sellItem.setTransportTicketItemId(ticket);
                    //     providerRepository.saveSellableItem(sellItem);
                    // }
                    ItemStatus itemStatus = ItemStatus.SUSPENDED;
                    if (ServiceStatus.ACTIVATE.equals(status)) {
                        itemStatus = (tDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK;
                    }

                    SellableItems sellItem = createUnifiedSellableItemCustom(service, tDto.getPrice(), tDto.getAvailableSlots(), itemStatus);
                    sellItem.setTransportTicketItemId(ticket);
                    providerRepository.saveSellableItem(sellItem);
                }
            }
        }
        return service.getId();
    }
    
    private SellableItems createUnifiedSellableItemCustom(Services service, BigDecimal price, int slots, ItemStatus status) {
        SellableItems sellItem = new SellableItems();
        sellItem.setServiceId(service);
        sellItem.setPrice(price);
        sellItem.setAvailableSlots(slots);
        sellItem.setItemStatus(status); 
        sellItem.setCreatedAt(new Date());
        return sellItem;
    }

    @Override
    @Transactional
    public void addSubItemCustom(String username, Long serviceId, String serviceTypeStr, BaseComprehensiveRequest req) {
        Services service = providerRepository.getServiceById(serviceId);
        if (service == null) throw new RuntimeException("Lỗi: Dịch vụ gốc không tồn tại!");

        ServiceType type = ServiceType.valueOf(serviceTypeStr.toUpperCase());
        SellableItems sellItem = new SellableItems();
        sellItem.setServiceId(service);
        sellItem.setCreatedAt(new Date());

        ItemStatus targetStatus = ItemStatus.SUSPENDED;
        
        if (ServiceStatus.ACTIVATE.equals(service.getStatus())) {
            targetStatus = ItemStatus.AVAILABLE; 
        }
        sellItem.setItemStatus(targetStatus);

        if (type == ServiceType.TOUR && req instanceof TourComprehensiveRequest) {
            TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;
            if (tourReq.getTourSchedules() != null && !tourReq.getTourSchedules().isEmpty()) {
                TourComprehensiveRequest.ScheduleInnerDTO sDto = tourReq.getTourSchedules().get(0);
                TourItemConcs schedule = new TourItemConcs();
                schedule.setTourDetailId(service.getTourDetails());
                schedule.setDepartureTime(sDto.getDepartureTime());
                schedule.setReturnTime(sDto.getReturnTime());
                schedule.setMaxParticipants(sDto.getMaxParticipants());
                providerRepository.saveTourSchedule(schedule);
                
                sellItem.setPrice(sDto.getPrice());
                sellItem.setAvailableSlots(sDto.getAvailableSlots());
                sellItem.setTourItemConcId(schedule);
                
                if (ServiceStatus.ACTIVATE.equals(service.getStatus())) {
                    sellItem.setItemStatus((sDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
                } else {
                    sellItem.setItemStatus(ItemStatus.SUSPENDED);
                }
            }
        } else if (type == ServiceType.HOTEL && req instanceof HotelComprehensiveRequest) {
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
                
                sellItem.setPrice(rDto.getPrice());
                sellItem.setAvailableSlots(rDto.getAvailableSlots());
                sellItem.setHotelRoomItemId(room);
                
                if (ServiceStatus.ACTIVATE.equals(service.getStatus())) {
                    sellItem.setItemStatus((rDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
                } else {
                    sellItem.setItemStatus(ItemStatus.SUSPENDED);
                }
            }
        } else if (type == ServiceType.TRANSPORT && req instanceof TransportComprehensiveRequest) {
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
                
                sellItem.setPrice(tDto.getPrice());
                sellItem.setAvailableSlots(tDto.getAvailableSlots());
                sellItem.setTransportTicketItemId(ticket);
                
                if (ServiceStatus.ACTIVATE.equals(service.getStatus())) {
                    sellItem.setItemStatus((tDto.getAvailableSlots() > 0) ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
                } else {
                    sellItem.setItemStatus(ItemStatus.SUSPENDED);
                }
            }
        }
        providerRepository.saveSellableItem(sellItem);
    }

    //PATCH /services/{id} đổi qua suppended
    @Override
    @Transactional
    public void updateServiceStatus(String username, Long id, String statusStr) {
       Services service = providerRepository.getServiceById(id); 
        if (service == null) throw new RuntimeException("Lỗi: Dịch vụ không tồn tại!");

        ServiceStatus newStatus = ServiceStatus.valueOf(statusStr.toUpperCase());
        ServiceStatus currentStatus = service.getStatus();

        if (ServiceStatus.DELETED.equals(currentStatus)) {
            throw new RuntimeException("Thao tác bị từ chối. Bài viết này đã bị XÓA VĨNH VIỄN (DELETED), không được phép thay đổi trạng thái nữa!");
        }
        // nếu đang là DRAFT mà muốn chuyển sang ACTIVATE/SUSPENDED thì ok, nhưng đã từng là ACTIVATE/SUSPENDED rồi thì không DRAFT nữa
        if (newStatus == ServiceStatus.DRAFT) {
            if (currentStatus == ServiceStatus.ACTIVATE || currentStatus == ServiceStatus.SUSPENDED) {
                throw new RuntimeException("Thao tác bị từ chối: Bài viết đã từng công khai, không thể hạ cấp quay xe về làm bản nháp (DRAFT)!");
            }
        }

        service.setStatus(newStatus);
        service.setUpdatedAt(new Date());
        if (ServiceStatus.DELETED.equals(newStatus)) {
            service.setDeletedAt(new Date());
        }
        providerRepository.updateService(service);

        if (ServiceStatus.SUSPENDED.equals(newStatus) || ServiceStatus.DELETED.equals(newStatus)) {
            providerRepository.updateAllSellableStatusByService(id, ItemStatus.SUSPENDED);
            providerRepository.updateService(service);
        }

        else if (ServiceStatus.ACTIVATE.equals(newStatus)) {
            Set<SellableItems> sellableItemsSet = service.getSellableItemsSet();
            if (sellableItemsSet != null && !sellableItemsSet.isEmpty()) {
                for (SellableItems item : sellableItemsSet) {
                    
                    // Nếu gói nào trước đó bị Provider chủ động khóathì giữ SUSPENDED
                    if (ItemStatus.SUSPENDED.equals(item.getItemStatus()) || item.getItemStatus() == null) {
                        if (item.getAvailableSlots() > 0) {
                            item.setItemStatus(ItemStatus.AVAILABLE); // Còn chỗ trống ──► Bật AVAILABLE mở bán!
                        } else {
                            item.setItemStatus(ItemStatus.OUT_OF_STOCK); // Hết sạch chỗ ──► Ép sang OUT_OF_STOCK báo hết hàng!
                        }
                    }
                }
            }
                providerRepository.updateService(service);
            }
    }

    //PUT /services/{id} -> Update -> CHUYỂN SANG PUBLISH
    @Override
    @Transactional
    public void updateComprehensiveService(String username, Long id, BaseComprehensiveRequest req) {
        ServiceType type = ServiceType.valueOf(req.getServiceType().toUpperCase());
        Services service = providerRepository.getServiceDetailByIdAndType(id, type);
        if (service == null) throw new RuntimeException("Lỗi: Bài đăng không tồn tại trên sàn!");

        if (ServiceStatus.DELETED.equals(service.getStatus())) {
            throw new RuntimeException("Thao tác bị từ chối nghiêm trọng: Dịch vụ này đã bị XÓA VĨNH VIỄN (DELETED), không được phép cập nhật hay thay đổi dữ liệu nữa!");
        }

        service.setName(req.getName());
        service.setDescription(req.getDescription());
        service.setUpdatedAt(new Date());
        service.setStatus(ServiceStatus.ACTIVATE); 
        providerRepository.updateService(service);

        if (req.getCategoryIds() != null) {
            Set<Categories> categoriesSet = new java.util.HashSet<>();
            
            for (Long cateId : req.getCategoryIds()) {
                Categories cate = providerRepository.getCategoryById(cateId);
                if (cate != null) {
                    if (!cate.getServiceType().equals(type)) {
                        throw new RuntimeException("Lỗi cấu trúc: Danh mục [ID: " + cateId + " - " + cate.getName() 
                                + "] thuộc phân hệ " + cate.getServiceType() + " không được phép gán vào dịch vụ kiểu " + type + "!");
                    }
                    if (cate.getServicesSet() == null) {
                        cate.setServicesSet(new java.util.HashSet<>());
                    }
                    cate.getServicesSet().add(service);
                    categoriesSet.add(cate);
                }
            }
            service.setCategoriesSet(categoriesSet);
        }


        if (type == ServiceType.TOUR && req instanceof TourComprehensiveRequest) {
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
                // Lấy tập hợp Set đang có dưới RAM để tìm dòng con tương ứng sửa đổi
                if (tour.getTourItemConcsSet() != null && !tour.getTourItemConcsSet().isEmpty()) {
                    schedule.setDepartureTime(sDto.getDepartureTime());
                    schedule.setReturnTime(sDto.getReturnTime());
                    schedule.setMaxParticipants(sDto.getMaxParticipants());
                    providerRepository.saveTourSchedule(schedule);
                }
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
        else if (type == ServiceType.HOTEL && req instanceof HotelComprehensiveRequest) {
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

            if (hotelReq.getHotelRooms() != null && !hotelReq.getHotelRooms().isEmpty()) {
                HotelComprehensiveRequest.RoomInnerDTO rDto = hotelReq.getHotelRooms().get(0);
                if (hotel.getHotelRoomItemsSet() != null && !hotel.getHotelRoomItemsSet().isEmpty()) {
                    HotelRoomItems room = null;      
                    if (hotel != null && hotel.getHotelRoomItemsSet() != null && !hotel.getHotelRoomItemsSet().isEmpty()) {
                    room = hotel.getHotelRoomItemsSet().iterator().next();
                } else {
                    room = new HotelRoomItems();
                    room.setHotelDetailId(hotel);
                }

                    room.setRoomType(rDto.getRoomType());
                    room.setCapacity(rDto.getCapacity());
                    room.setBedType(rDto.getBedType());
                    room.setRoomSizeM2(rDto.getRoomSizeM2());
                    room.setRoomAmenities(rDto.getRoomAmenities());
                    providerRepository.saveHotelRoomItem(room);

                    if (service.getSellableItemsSet() == null || service.getSellableItemsSet().isEmpty()) {
                    SellableItems newSellItem = new SellableItems();
                    newSellItem.setServiceId(service);
                    newSellItem.setHotelRoomItemId(room);
                    newSellItem.setCreatedAt(new Date());
                    if (service.getSellableItemsSet() == null) service.setSellableItemsSet(new HashSet<>());
                    service.getSellableItemsSet().add(newSellItem);
                }
                }
            }
        } 
        else if (type == ServiceType.TRANSPORT && req instanceof TransportComprehensiveRequest) {
            TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;
            
            TransportDetails trans = service.getTransportDetails();
            if (trans != null) {
                trans.setBrandName(transReq.getBrandName());
                trans.setVehicleType(transReq.getVehicleType());
                trans.setDepartureStation(transReq.getDepartureStation());
                trans.setArrivalStation(transReq.getArrivalStation());
                providerRepository.saveTransportDetails(trans);
            }

            if (transReq.getTransportTickets() != null && !transReq.getTransportTickets().isEmpty()) {
                TransportComprehensiveRequest.TicketInnerDTO tDto = transReq.getTransportTickets().get(0);
                if (trans.getTransportTicketItemsSet() != null && !trans.getTransportTicketItemsSet().isEmpty()) {
                    TransportTicketItems ticket = null;
                    if (trans != null && trans.getTransportTicketItemsSet() != null && !trans.getTransportTicketItemsSet().isEmpty()) {
                    ticket = trans.getTransportTicketItemsSet().iterator().next();
                } else {
                    ticket = new TransportTicketItems();
                    ticket.setTransportDetailId(trans);
                }

                    ticket.setDepartureTime(tDto.getDepartureTime());
                    ticket.setArrivalTime(tDto.getArrivalTime());
                    ticket.setDurationMinutes(tDto.getDurationMinutes());
                    ticket.setSeatClass(tDto.getSeatClass());
                    providerRepository.saveTransportTicketItem(ticket);

                    if (service.getSellableItemsSet() == null || service.getSellableItemsSet().isEmpty()) {
                    SellableItems newSellItem = new SellableItems();
                    newSellItem.setServiceId(service);
                    newSellItem.setTransportTicketItemId(ticket);
                    newSellItem.setCreatedAt(new Date());
                    if (service.getSellableItemsSet() == null) service.setSellableItemsSet(new HashSet<>());
                    service.getSellableItemsSet().add(newSellItem);
                }
                }
            }
        }

        Set<SellableItems> sellableItemsSet = service.getSellableItemsSet();
        if (sellableItemsSet != null && !sellableItemsSet.isEmpty()) {
            for (SellableItems item : sellableItemsSet) {
                
                if (type == ServiceType.TOUR && req instanceof TourComprehensiveRequest) {
                    TourComprehensiveRequest.ScheduleInnerDTO sDto = ((TourComprehensiveRequest) req).getTourSchedules().get(0);
                    item.setPrice(sDto.getPrice());
                    item.setAvailableSlots(sDto.getAvailableSlots());
                } else if (type == ServiceType.HOTEL && req instanceof HotelComprehensiveRequest) {
                    HotelComprehensiveRequest.RoomInnerDTO rDto = ((HotelComprehensiveRequest) req).getHotelRooms().get(0);
                    item.setPrice(rDto.getPrice());
                    item.setAvailableSlots(rDto.getAvailableSlots());
                } else if (type == ServiceType.TRANSPORT && req instanceof TransportComprehensiveRequest) {
                    TransportComprehensiveRequest.TicketInnerDTO tDto = ((TransportComprehensiveRequest) req).getTransportTickets().get(0);
                    item.setPrice(tDto.getPrice());
                    item.setAvailableSlots(tDto.getAvailableSlots());
                }

                if (ItemStatus.SUSPENDED.equals(item.getItemStatus()) || item.getItemStatus() == null) {
                    if (item.getAvailableSlots() > 0) {
                        item.setItemStatus(ItemStatus.AVAILABLE);
                    } else {
                        item.setItemStatus(ItemStatus.OUT_OF_STOCK); 
                    }
                }
            }
            providerRepository.updateService(service);
        }
    }

    @Override
    @Transactional
    public void updateServiceImages(String username, Long serviceId, List<Long> retainImageIds, MultipartFile[] newFiles) {
        Services service = providerRepository.getServiceById(serviceId);
        if (service == null) throw new RuntimeException("Lỗi: Bài đăng không tồn tại trên hệ thống!");

        Set<ServiceImages> currentImages = service.getServiceImagesSet();
        if (currentImages != null && !currentImages.isEmpty()) {
            java.util.Iterator<ServiceImages> iterator = currentImages.iterator();
            while (iterator.hasNext()) {
                ServiceImages img = iterator.next();
                                if (retainImageIds == null || !retainImageIds.contains(img.getId())) {
                    try {
                        String url = img.getImageUrl();
                        if (url.contains("cloudinary.com")) {
                            String fileNameWithExt = url.substring(url.lastIndexOf("/") + 1);
                            String publicId = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."));
                            
                            // Bắn lệnh hủy file trên Cloudinary
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xóa ảnh trên Cloudinary: " + e.getMessage());
                    }
                    
                    providerRepository.removeServiceImage(img);
                    iterator.remove();
                }
            }
        }

        if (newFiles != null && newFiles.length > 0) {
            for (MultipartFile file : newFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                com.cloudinary.utils.ObjectUtils.emptyMap()
                        );
                        String secureUrl = (String) uploadResult.get("secure_url");

                        ServiceImages newImg = new ServiceImages();
                        newImg.setServiceId(service);
                        newImg.setImageUrl(secureUrl);
                        newImg.setIsThumbnail(false); 
                        
                        providerRepository.saveServiceImage(newImg);
                        if (service.getServiceImagesSet() == null) {
                            service.setServiceImagesSet(new HashSet<>());
                        }
                        service.getServiceImagesSet().add(newImg); 
                    } catch (java.io.IOException e) {
                        throw new RuntimeException("Lỗi cơ học đường truyền khi upload mảng file mới!");
                    }
                }
            }
        }

        Set<ServiceImages> aliveImages = service.getServiceImagesSet();
        if (aliveImages != null && !aliveImages.isEmpty()) {
            boolean hasThumbnail = aliveImages.stream().anyMatch(img -> img.getIsThumbnail() != null && img.getIsThumbnail());
            if (!hasThumbnail) {
                ServiceImages firstImg = aliveImages.iterator().next();
                firstImg.setIsThumbnail(true);
                providerRepository.updateService(service); 
            }
        }
    }


    @Override
    @Transactional
    public void softDeleteSubItem(String username, Long serviceId, String serviceTypeStr, Long subItemId) {
        String targetField = "TOUR".equalsIgnoreCase(serviceTypeStr) ? "tourItemConcId" : 
                             "HOTEL".equalsIgnoreCase(serviceTypeStr) ? "hotelRoomItemId" : "transportTicketItemId";

        // Trạng thái sellable về SUSPENDED để ẩn 
        providerRepository.updateSingleSellableStatus(serviceId, targetField, subItemId, ItemStatus.SUSPENDED);
    }

    
    @Override
    @Transactional
    public void updateSubItem(String username,Long serviceId,String serviceTypeStr,Long subItemId,UpdateSubItemRequest req) {

    ServiceType type =ServiceType.valueOf(serviceTypeStr.toUpperCase());
    Services service =providerRepository.getServiceById(serviceId);

    switch (type) {
        case HOTEL -> {

            HotelRoomItems room =providerRepository.getRoomById(subItemId);

           if (room == null) {
                    throw new RuntimeException("Room không tồn tại!");
                }
            if (!room.getHotelDetailId().getServiceId().equals(service.getId())) {
                    throw new RuntimeException("Vi phạm bảo mật: Room không thuộc về service này!");
                }
            if (req.getRoomType() != null) room.setRoomType(req.getRoomType());
                if (req.getCapacity() != null) room.setCapacity(req.getCapacity());
                if (req.getBedType() != null) room.setBedType(req.getBedType());
                if (req.getRoomSizeM2() != null) room.setRoomSizeM2(req.getRoomSizeM2());
                if (req.getRoomAmenities() != null) room.setRoomAmenities(req.getRoomAmenities());

            providerRepository.saveHotelRoomItem(room);

            SellableItems item =room.getSellableItems();
        
            if (item != null) {
                    if (req.getPrice() != null) item.setPrice(req.getPrice());
                    if (req.getAvailableSlots() != null) {
                        item.setAvailableSlots(req.getAvailableSlots());
                        // Nhìn kho slots mới cập nhật để bẻ lái trạng thái
                        item.setItemStatus(req.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
                    }
                    providerRepository.saveSellableItem(item);
                }
        }

        case TOUR -> {

            TourItemConcs schedule =providerRepository.getTourScheduleById(subItemId);

            if (schedule == null) {
                throw new RuntimeException("Schedule không tồn tại");
            }

            if (!schedule.getTourDetailId().getServiceId().equals(service.getId())) {

                throw new RuntimeException("Schedule không thuộc service này");
            }

            if (req.getDepartureTime() != null) schedule.setDepartureTime(req.getDepartureTime());
            if (req.getReturnTime() != null) schedule.setReturnTime(req.getReturnTime());
            if (req.getMaxParticipants() != null) schedule.setMaxParticipants(req.getMaxParticipants());

            providerRepository.saveTourSchedule(schedule);
            SellableItems item =schedule.getSellableItems();
        
            if (item != null) {
                    if (req.getPrice() != null) item.setPrice(req.getPrice());
                    if (req.getAvailableSlots() != null) {
                        item.setAvailableSlots(req.getAvailableSlots());
                        item.setItemStatus(req.getAvailableSlots() > 0 ? ItemStatus.AVAILABLE : ItemStatus.OUT_OF_STOCK);
                    }
                    providerRepository.saveSellableItem(item);
                }
        }

        case TRANSPORT -> {

            TransportTicketItems ticket = providerRepository.getTransportTicketById(subItemId);
                if (ticket == null) {
                    throw new RuntimeException("Ticket không tồn tại!");
                }
                if (!ticket.getTransportDetailId().getServiceId().equals(service.getId())) {
                    throw new RuntimeException("Vi phạm bảo mật: Ticket không thuộc về service này!");
                }

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
    }
}

}
