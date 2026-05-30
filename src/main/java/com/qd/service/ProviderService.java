package com.qd.service;

import java.util.Map;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProviderService {
    Map<String, Object> getMyServicesList(String username, Map<String, String> params);
    Object getMyServiceDetail(String username, Long id, String typeStr);
    Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req, MultipartFile[] files);

    void updateComprehensiveService(String username, Long id, BaseComprehensiveRequest req);
    void updateServiceStatus(String username, Long id, String statusStr);

    void updateServiceImages(String username, Long serviceId, List<Long> retainImageIds, MultipartFile[] newFiles);

    void softDeleteSubItem(String username, Long serviceId, String serviceTypeStr, Long subItemId);
    void addSubItemCustom(String username, Long serviceId, String serviceTypeStr, BaseComprehensiveRequest req);
    void updateSubItem(String username,Long serviceId,String serviceTypeStr,Long subItemId,UpdateSubItemRequest req);
    // void addSubItem(String username, Long serviceId, BaseComprehensiveRequest req);
    // void updateSubItemStatusAndPrice(String username, Long serviceId, String type, Long subItemId, String itemStatusStr, BigDecimal price, Integer slots);
    // void softDeleteService(String username, Long id);
    // void updateServiceImages(String username, Long serviceId, List<Long> retainImageIds, MultipartFile[] newFiles); 
}
