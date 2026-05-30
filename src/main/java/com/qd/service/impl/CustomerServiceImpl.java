package com.qd.service.impl;

import com.qd.pojo.Categories;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.repository.CustomerRepository;
import com.qd.repository.ProviderRepository;
import com.qd.service.CustomerService;

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
    private Environment env;

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

        int pageSize = Integer.parseInt(this.env.getProperty("services.page_size", "10"));
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
    public Map<String, Object> getServiceMainDetail(Long id) {
        Services s = providerRepository.getServiceById(id);
        if (s == null || !com.qd.enums.ServiceStatus.ACTIVATE.equals(s.getStatus())) {
            throw new RuntimeException("Lỗi: Dịch vụ không tồn tại hoặc chưa được mở bán!");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("serviceId", s.getId());
        data.put("name", s.getName());
        data.put("description", s.getDescription());
        data.put("serviceType", s.getServiceType().toString());
        data.put("bookingCount", s.getBookingCount());
        data.put("averageRating", s.getAverageRating());

        if (s.getServiceType() == com.qd.enums.ServiceType.HOTEL && s.getHotelDetails() != null) {
            Map<String, Object> hotel = new HashMap<>();
            hotel.put("starRating", s.getHotelDetails().getStarRating());
            hotel.put("address", s.getHotelDetails().getAddress());
            hotel.put("city", s.getHotelDetails().getCity());
            hotel.put("amenities", s.getHotelDetails().getAmenities());
            data.put("hotelDetails", hotel);
        } else if (s.getServiceType() == com.qd.enums.ServiceType.TOUR && s.getTourDetails() != null) {
            Map<String, Object> tour = new HashMap<>();
            tour.put("departure", s.getTourDetails().getDepartureLocation());
            tour.put("destination", s.getTourDetails().getDestinationLocation());
            tour.put("days", s.getTourDetails().getDurationDays());
            tour.put("nights", s.getTourDetails().getDurationNights());
            data.put("tourDetails", tour);
        }

        List<String> album = s.getServiceImagesSet().stream()
                .map(ServiceImages::getImageUrl).collect(Collectors.toList());
        data.put("albumImages", album);
        data.put("providerCompany", s.getProviderId().getCompanyName());

        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getServiceSubItemsAndReviews(Long id) {
        Services s = providerRepository.getServiceById(id);
        if (s == null) throw new RuntimeException("Dịch vụ không tồn tại!");
        
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> subItems = s.getSellableItemsSet().stream()
            .filter(item -> com.qd.enums.ItemStatus.AVAILABLE.equals(item.getItemStatus()))
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("sellableItemId", item.getId());
                itemMap.put("price", item.getPrice());
                itemMap.put("availableSlots", item.getAvailableSlots());
                
                if (item.getHotelRoomItemId() != null) {
                    itemMap.put("subItemName", item.getHotelRoomItemId().getRoomType());
                    itemMap.put("details", "Sức chứa: " + item.getHotelRoomItemId().getCapacity() + " giường: " + item.getHotelRoomItemId().getBedType());
                } else if (item.getTourItemConcId() != null) {
                    itemMap.put("subItemName", "Lịch lịch trình khởi hành lẻ");
                    itemMap.put("details", "Khởi hành: " + item.getTourItemConcId().getDepartureTime());
                }
                return itemMap;
            }).collect(Collectors.toList());
        result.put("sellableGiaoDienList", subItems);

        List<Map<String, Object>> feedbackList = new ArrayList<>();
        if (s.getSellableItemsSet() != null) {
            for (SellableItems item : s.getSellableItemsSet()) {
                if (item.getOrderDetailsSet() != null) {
                    for (OrderDetails od : item.getOrderDetailsSet()) {
                        if (od.getReviews() != null) {
                            Map<String, Object> rMap = new HashMap<>();
                            rMap.put("reviewId", od.getReviews().getId());
                            rMap.put("ratingStar", od.getReviews().getRating());
                            rMap.put("commentText", od.getReviews().getComment());
                            rMap.put("clientName", od.getReviews().getUserId().getFullName());
                            rMap.put("reviewDate", od.getReviews().getCreatedAt());
                            feedbackList.add(rMap);
                        }
                    }
                }
            }
        }
        result.put("customerReviewsFeedback", feedbackList);
        return result;
    }


}
