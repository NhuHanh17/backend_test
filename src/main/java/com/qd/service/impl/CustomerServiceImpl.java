package com.qd.service.impl;

import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import com.qd.pattern.compare.CompareServicesStrategy;
import com.qd.pattern.compare.CompareStrategyFactory;
import com.qd.pojo.Categories;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.pojo.Users;
import com.qd.repository.CustomerRepository;
import com.qd.repository.FavoriteRepository;
import com.qd.repository.ProviderRepository;
import com.qd.repository.UserRepository;
import com.qd.service.CustomerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment env;

    @Autowired
    private CompareStrategyFactory compareStrategyFactory;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getServicesForCustomer(Map<String, String> params) {
        List<Services> list = customerRepository.getServicesForCustomer(params);
        Long totalElements = customerRepository.countServices(params);

        List<Map<String, Object>> content = list.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", s.getId());
            map.put("name", s.getName());
            map.put("serviceType", s.getServiceType().toString());
            map.put("averageRating", s.getAverageRating());
            map.put("reviewCount", s.getReviewCount());

            BigDecimal minPrice = BigDecimal.ZERO;
            if (s.getSellableItemsSet() != null && !s.getSellableItemsSet().isEmpty()) {
                minPrice = s.getSellableItemsSet().stream()
                    .map(SellableItems::getPrice) 
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo) 
                    .orElse(BigDecimal.ZERO);
            }
            map.put("price", minPrice);

            String thumbnail = s.getServiceImagesSet().stream()
                .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail())
                .map(ServiceImages::getImageUrl).findFirst().orElse("");
            map.put("thumbnailUrl", thumbnail);
            
            map.put("providerName", s.getProviderId().getCompanyName());

            List<Map<String, Object>> cateList = new ArrayList<>();
            if (s.getCategoriesSet() != null && !s.getCategoriesSet().isEmpty()) {
                for (Categories cat : s.getCategoriesSet()) {
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("categoryId", cat.getId());
                    catMap.put("categoryName", cat.getName());
                    cateList.add(catMap);
                }
            }
            map.put("categories", cateList);
            return map;
        }).collect(Collectors.toList());

        int pageSize = Integer.parseInt(this.env.getProperty("services.page_size", "20"));
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
    public Map<String, Object> getServiceMainDetail(Long id, String currentUsername) {
        Services s = providerRepository.getServiceById(id);
        if (s == null || !ServiceStatus.ACTIVATE.equals(s.getStatus())) {
            throw new RuntimeException(" Dịch vụ không tồn tại hoặc chưa được mở bán!");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("serviceId", s.getId());
        data.put("name", s.getName());
        data.put("description", s.getDescription());
        data.put("serviceType", s.getServiceType().toString());
        data.put("bookingCount", s.getBookingCount());
        data.put("averageRating", s.getAverageRating());

        if (s.getServiceType() == ServiceType.HOTEL && s.getHotelDetails() != null) {
            Map<String, Object> hotel = new HashMap<>();
            hotel.put("starRating", s.getHotelDetails().getStarRating());
            hotel.put("address", s.getHotelDetails().getAddress());
            hotel.put("city", s.getHotelDetails().getCity());
            hotel.put("amenities", s.getHotelDetails().getAmenities());
            data.put("hotelDetails", hotel);
        } else if (s.getServiceType() == ServiceType.TOUR && s.getTourDetails() != null) {
            Map<String, Object> tour = new HashMap<>();
            tour.put("departure", s.getTourDetails().getDepartureLocation());
            tour.put("destination", s.getTourDetails().getDestinationLocation());
            tour.put("days", s.getTourDetails().getDurationDays());
            tour.put("nights", s.getTourDetails().getDurationNights());
            data.put("tourDetails", tour);
        }else if (s.getServiceType() == ServiceType.TRANSPORT && s.getTransportDetails() != null) {
            Map<String, Object> transport = new HashMap<>();
            transport.put("brandName", s.getTransportDetails().getBrandName());
            transport.put("vehicleType", s.getTransportDetails().getVehicleType()); 
            transport.put("departureLocation", s.getTransportDetails().getDepartureStation());
            transport.put("arrivalLocation", s.getTransportDetails().getArrivalStation());          
            data.put("transportDetails", transport);
        }

        List<String> album = s.getServiceImagesSet().stream()
                .map(ServiceImages::getImageUrl).collect(Collectors.toList());
        data.put("albumImages", album);
        data.put("providerCompany", s.getProviderId().getCompanyName());

        boolean isFavorited = false;
        if (currentUsername != null && !currentUsername.trim().isEmpty()) {
            Users user = userRepository.findByUsername(currentUsername);
            if (user != null) {
                isFavorited = favoriteRepository.isServiceFavorited(user.getId(), s.getId());
            }
            data.put("isFavorited", isFavorited);
        }else {
            data.put("isFavorited", "null");
        }
        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getServiceSubItems(Long id) {
        Services s = providerRepository.getServiceById(id);
        if (s == null) throw new RuntimeException("Lỗi: Dịch vụ lữ hành không tồn tại trên hệ thống!");
        
        List<Map<String, Object>> subItems = new ArrayList<>();
        
        if (s.getSellableItemsSet() != null && !s.getSellableItemsSet().isEmpty()) {
            subItems = s.getSellableItemsSet().stream()
                .filter(item -> ItemStatus.AVAILABLE.equals(item.getItemStatus()))
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("sellableItemId", item.getId());
                    itemMap.put("price", item.getPrice());
                    
                    itemMap.put("availableSlots", item.getAvailableSlots());
                    itemMap.put("itemStatus", item.getItemStatus().toString());
                       
                    if (item.getHotelRoomItemId() != null) {
                        var hotelRoom = item.getHotelRoomItemId();
                        itemMap.put("subItemName", hotelRoom.getRoomType()); 
                        itemMap.put("details", "Sức chứa: " + hotelRoom.getCapacity() 
                                + " người - Giường: " + hotelRoom.getBedType() 
                                + " - Diện tích: " + hotelRoom.getRoomSizeM2() + "m²");
                    } 
                    
                    else if (item.getTourItemConcId() != null) {
                        var tourDetail = item.getTourItemConcId();
                        itemMap.put("subItemName", "Lịch trình khởi hành Tour lẻ");
                        itemMap.put("details", "Khởi hành: " + tourDetail.getDepartureTime() 
                                + " - Giới hạn: " + tourDetail.getMaxParticipants() + " khách/đoàn");
                    } 
                                        else if (item.getTransportTicketItemId() != null) {
                        var transTicket = item.getTransportTicketItemId();
                        itemMap.put("subItemName", "Vé tuyến lữ hành hành trình");
                        itemMap.put("details", "Hạng chỗ: " + transTicket.getSeatClass() 
                                + " - Xuất bến lúc: " + transTicket.getDepartureTime());
                    }
                    
                    return itemMap;
                }).collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sellableGiaoDienList", subItems);

        return result;
        }

        @Override
        @Transactional(readOnly = true)
        public List<Map<String, Object>> getPaymentMethodsForCheckout() {
        List<PaymentMethods> list = customerRepository.getActivePaymentMethods();
        return list.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("methodId", m.getId());
            map.put("methodName", m.getMethodName());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMyOrdersHistory(String username, Map<String, String> params) {
        List<Orders> ordersList = customerRepository.getCustomerOrdersHistory(username, params);
        Long totalElements = customerRepository.countCustomerOrders(username);
        
        List<Map<String, Object>> content = ordersList.stream().map(o -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", o.getId());
            orderMap.put("totalAmount", o.getTotalAmount());
            orderMap.put("paymentStatus", o.getPaymentStatus().toString());
            orderMap.put("transactionReference", o.getTransactionReference());
            orderMap.put("createdAt", o.getCreatedAt() != null ? o.getCreatedAt().getTime() : null);
            orderMap.put("paymentMethod", o.getPaymentMethodId().getMethodName());
            orderMap.put("providerName", o.getProviderId().getCompanyName());

            List<Map<String, Object>> details = o.getOrderDetailsSet().stream().map(od -> {
                Map<String, Object> odMap = new HashMap<>();
                odMap.put("detailId", od.getId());
                odMap.put("quantity", od.getQuantity());
                odMap.put("priceSnapshot", od.getPrice());
                odMap.put("bookingStatus", od.getBookingStatus().toString());
                odMap.put("serviceName", od.getServiceNameSnapshot());
                odMap.put("itemDescription", od.getItemDescriptionSnapshot());
                
                odMap.put("isReviewed", od.getReviews() != null);
                return odMap;
            }).collect(Collectors.toList());

            orderMap.put("orderDetailsList", details);
            return orderMap;
        }).collect(Collectors.toList());

       int pageSize = Integer.parseInt(this.env.getProperty("my_orders.page_size", "20"));
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
    public List<Map<String, Object>> compareServices(List<Long> ids) {
        if (ids == null || ids.size() < 2) {
            throw new RuntimeException("Vui lòng chọn ít nhất 2 dịch vụ cùng loại để so sánh!");
        }
        List<Services> list = customerRepository.getServicesForComparison(ids);
        if (list.isEmpty()) throw new RuntimeException("Không tìm thấy các dịch vụ hợp lệ!");

        com.qd.enums.ServiceType firstType = list.get(0).getServiceType();
        boolean isSameType = list.stream().allMatch(s -> s.getServiceType() == firstType);
        if (!isSameType) {
            throw new RuntimeException("Hệ thống chỉ hỗ trợ so sánh các dịch vụ cùng loại  với nhau!");
        }
        CompareServicesStrategy compareStrategy = compareStrategyFactory.getStrategy(firstType);
        return list.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", s.getId());
            map.put("name", s.getName());
            map.put("averageRating", s.getAverageRating());
            map.put("reviewCount", s.getReviewCount());
            map.put("providerName", s.getProviderId().getCompanyName());

            BigDecimal minPrice = s.getSellableItemsSet().stream()
                    .filter(item -> item.getPrice() != null)
                    .map(SellableItems::getPrice)
                    .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            map.put("minPrice", minPrice);

            String thumbnail = s.getServiceImagesSet().stream()
                    .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail())
                    .map(ServiceImages::getImageUrl).findFirst().orElse("");
            map.put("thumbnailUrl", thumbnail);
            map.put("specifications", compareStrategy.extractSpecifications(s)); 

            return map;
        }).collect(Collectors.toList());
    }
}
