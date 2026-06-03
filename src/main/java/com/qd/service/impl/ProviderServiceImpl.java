package com.qd.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;

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
import com.qd.pattern.service.ComprehensiveServiceFactory;
import com.qd.pattern.service.ComprehensiveServiceStrategy;
import com.qd.pojo.Categories;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.Providers;
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

import java.util.ArrayList;
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
    private ComprehensiveServiceFactory serviceFactory;

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
        if (isPublishAction)  status = ServiceStatus.ACTIVATE;

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
                Categories cate = providerRepository.getCategoryById(cateId); 
                if (cate != null) {
                    if (!cate.getServiceType().equals(type)) {
                        throw new RuntimeException(" Danh mục " + cate.getName() 
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
                                file.getBytes(),ObjectUtils.emptyMap());
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
                        throw new RuntimeException("Lỗi upload các ảnh lên Cloudinary!");
                    }
                }
            }
        }
        providerRepository.saveService(service);
        ComprehensiveServiceStrategy strategy = serviceFactory.getStrategy(type);
        strategy.saveDetails(service, req);
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
        if (service == null) throw new RuntimeException("Dịch vụ gốc không tồn tại!");

        ServiceType type = ServiceType.valueOf(serviceTypeStr.toUpperCase());
        serviceFactory.getStrategy(type).addSubItem(service, req);
    }

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
        if (newStatus == ServiceStatus.DRAFT) {
            if (currentStatus == ServiceStatus.ACTIVATE || currentStatus == ServiceStatus.SUSPENDED) {
                throw new RuntimeException("Thao tác bị từ chối: Bài viết đã từng công khai, không thể quay về làm bản nháp (DRAFT)!");
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
                    
                    if (ItemStatus.SUSPENDED.equals(item.getItemStatus()) || item.getItemStatus() == null) {
                        if (item.getAvailableSlots() > 0) {
                            item.setItemStatus(ItemStatus.AVAILABLE); 
                        } else {
                            item.setItemStatus(ItemStatus.OUT_OF_STOCK);
                        }
                    }
                }
            }
                providerRepository.updateService(service);
            }
    }

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

        serviceFactory.getStrategy(type).updateDetails(service, req);
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
            Iterator<ServiceImages> iterator = currentImages.iterator();
            while (iterator.hasNext()) {
                ServiceImages img = iterator.next();
                                if (retainImageIds == null || !retainImageIds.contains(img.getId())) {
                    try {
                        String url = img.getImageUrl();
                        if (url.contains("cloudinary.com")) {
                            String fileNameWithExt = url.substring(url.lastIndexOf("/") + 1);
                            String publicId = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."));
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

        boolean hasExistingThumbnail = false;
        if (service.getServiceImagesSet() != null) {
            hasExistingThumbnail = service.getServiceImagesSet().stream()
                    .anyMatch(img -> img.getIsThumbnail() != null && img.getIsThumbnail());
        }

        if (newFiles != null && newFiles.length > 0) {
            if (service.getServiceImagesSet() == null) {
                service.setServiceImagesSet(new HashSet<>());
            }

            boolean isFirstSlot = !hasExistingThumbnail;
            for (MultipartFile file : newFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),ObjectUtils.emptyMap());
                        String secureUrl = (String) uploadResult.get("secure_url");

                        ServiceImages newImg = new ServiceImages();
                        newImg.setServiceId(service);
                        newImg.setImageUrl(secureUrl);
                       if (isFirstSlot) {
                            newImg.setIsThumbnail(true);
                            isFirstSlot = false; 
                        } else {
                            newImg.setIsThumbnail(false);
                        }

                        providerRepository.saveServiceImage(newImg);
                        service.getServiceImagesSet().add(newImg);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi đường truyền khi upload mảng file mới!");
                    }
                }
            }
        }

        Set<ServiceImages> aliveImages = service.getServiceImagesSet();
        if (aliveImages != null && !aliveImages.isEmpty()) {
            boolean hasThumbnail = aliveImages.stream().anyMatch(img -> img.getIsThumbnail() != null && img.getIsThumbnail());
            if (!hasThumbnail) {
                ServiceImages rescueImg = aliveImages.iterator().next();
                rescueImg.setIsThumbnail(true);
                providerRepository.saveServiceImage(rescueImg);
            }
        }
        providerRepository.updateService(service); 
    }


    @Override
    @Transactional 
    public void updateServiceImagesAndStatusComprehensive(String username, Long serviceId, List<Long> retainImageIds, MultipartFile[] files, String status) {
        
        this.updateServiceImages(username, serviceId, retainImageIds, files);
        this.updateServiceStatus(username, serviceId, status);
    }

    @Override
    @Transactional
    public void softDeleteSubItem(String username, Long serviceId, String serviceTypeStr, Long subItemId) {
        String targetField = "TOUR".equalsIgnoreCase(serviceTypeStr) ? "tourItemConcId" : 
                             "HOTEL".equalsIgnoreCase(serviceTypeStr) ? "hotelRoomItemId" : "transportTicketItemId";

        providerRepository.updateSingleSellableStatus(serviceId, targetField, subItemId, ItemStatus.SUSPENDED);
    }

    
    @Override
    @Transactional
    public void updateSubItem(String username,Long serviceId,String serviceTypeStr,Long subItemId,UpdateSubItemRequest req) {

    ServiceType type =ServiceType.valueOf(serviceTypeStr.toUpperCase());
    Services service =providerRepository.getServiceById(serviceId);

    serviceFactory.getStrategy(type).updateSubItem(service, subItemId, req);
}

@Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrdersByProvider(String username, Map<String, String> params) {
        Providers provider = this.findProviderByUsername(username); 
        if (provider == null) throw new RuntimeException("Lỗi bảo mật: Bạn không phải là Đối tác hợp lệ!");

        List<Orders> list = providerRepository.getOrdersByProviderPaged(provider.getId(), params);
        Long totalElements = providerRepository.countOrdersByProvider(provider.getId(), params);
        List<Map<String, Object>> content = list.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", o.getId());
            map.put("totalAmount", o.getTotalAmount());
            map.put("paymentStatus", o.getPaymentStatus().toString());
            map.put("createdAt", o.getCreatedAt().getTime());
            map.put("transactionReference", o.getTransactionReference());
            map.put("paymentMethod", o.getPaymentMethodId().getMethodName()); 
            
            map.put("customerName", o.getUserId().getFullName());
            map.put("customerPhone", o.getUserId().getPhone());
            return map;
        }).collect(Collectors.toList());

        int pageSize = Integer.parseInt(this.env.getProperty("provider.order.page_size", "10"));
        int currentPage = (params != null && params.containsKey("page")) ? Integer.parseInt(params.get("page")) : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", totalElements);
        result.put("page", currentPage);
        result.put("size", pageSize);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProviderOrderDetail(String username, Long orderId) {
        Providers provider = this.findProviderByUsername(username);
        if (provider == null) throw new RuntimeException("Lỗi: Bạn không có quyền truy cập!");

        Orders o = providerRepository.getOrderByIdAndProvider(orderId, provider.getId());
        if (o == null) throw new RuntimeException("Đơn hàng không tồn tại hoặc không thuộc quyền sở hữu quản lý của bạn!");

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", o.getId());
        data.put("totalAmount", o.getTotalAmount());
        data.put("paymentStatus", o.getPaymentStatus().toString());
        data.put("transactionReference", o.getTransactionReference());
        data.put("createdAt", o.getCreatedAt());

        data.put("paymentMethodName", o.getPaymentMethodId().getMethodName());
        
        Map<String, Object> customer = new HashMap<>();
        customer.put("customerId", o.getUserId().getId());
        customer.put("fullName", o.getUserId().getFullName());
        customer.put("email", o.getUserId().getEmail());
        customer.put("phone", o.getUserId().getPhone());
        customer.put("address", o.getUserId().getAddress());
        data.put("customerInfo", customer); 

        List<Map<String, Object>> detailsList = new ArrayList<>();
        if (o.getOrderDetailsSet() != null) {
            for (OrderDetails od : o.getOrderDetailsSet()) {
                Map<String, Object> odMap = new HashMap<>();
                odMap.put("detailId", od.getId());
                odMap.put("quantity", od.getQuantity());
                odMap.put("priceSnapshot", od.getPrice()); 
                odMap.put("bookingStatus", od.getBookingStatus().toString());
                
                odMap.put("serviceNameSnapshot", od.getServiceNameSnapshot());
                odMap.put("providerNameSnapshot", od.getProviderNameSnapshot());
                odMap.put("itemDescriptionSnapshot", od.getItemDescriptionSnapshot());
                
                Map<String, Object> reviewMap = null;
                if (od.getReviews() != null) {
                    reviewMap = new HashMap<>();
                    reviewMap.put("reviewId", od.getReviews().getId());
                    reviewMap.put("ratingStar", od.getReviews().getRating()); 
                    reviewMap.put("commentText", od.getReviews().getComment()); 
                    reviewMap.put("reviewDate", od.getReviews().getCreatedAt()); 
                }
                odMap.put("reviewInfo", reviewMap);
                detailsList.add(odMap);
            }
        }
        data.put("orderDetailsList", detailsList);

        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public Providers findProviderByUsername(String username) {
        return this.providerRepository.findProviderByUsername(username);
    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProviderDashboardStats(String username, String periodType) {
        Users user = userRepository.findByUsername(username);
        if (user == null || user.getProviders() == null) {
            throw new RuntimeException("Lỗi: Đối tác không tồn tại trên hệ thống!");
        }
        Long providerId = user.getProviders().getId();
        List<Object[]> serviceData = providerRepository.getRevenueByService(providerId);
        List<Map<String, Object>> serviceStats = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalBookings = 0;

        for (Object[] row : serviceData) {
            Map<String, Object> item = new HashMap<>();
            item.put("serviceId", row[0]); 
            item.put("serviceName", row[1]);
            item.put("revenue", row[2]);
            item.put("bookingCount", row[3]);
            serviceStats.add(item);
            
            totalRevenue = totalRevenue.add((BigDecimal) row[2]);
            totalBookings += (Long) row[3];
        }

        List<Object[]> periodData = providerRepository.getRevenueByPeriod(providerId, periodType);
        List<Map<String, Object>> periodStats = new ArrayList<>();

        for (Object[] row : periodData) {
            Map<String, Object> item = new HashMap<>();
            if ("month".equalsIgnoreCase(periodType)) {
                item.put("periodLabel", "Tháng " + row[1] + "/" + row[0]);
                item.put("revenue", row[2]);
                item.put("orderCount", row[3]);
            } else if ("quarter".equalsIgnoreCase(periodType)) {
                item.put("periodLabel", "Quý " + row[1] + "/" + row[0]);
                item.put("revenue", row[2]);
                item.put("orderCount", row[3]);
            } else {
                item.put("periodLabel", "Năm " + row[0]);
                item.put("revenue", row[1]);
                item.put("orderCount", row[2]);
            }
            periodStats.add(item);
        }

        Map<String, Object> statsResult = new HashMap<>();
        statsResult.put("providerCompany", user.getProviders().getCompanyName());
        statsResult.put("summaryTotalRevenue", totalRevenue);
        statsResult.put("summaryTotalBookings", totalBookings); 
        statsResult.put("byServiceStats", serviceStats);
        statsResult.put("byPeriodStats", periodStats);

        return statsResult;
    }

}
