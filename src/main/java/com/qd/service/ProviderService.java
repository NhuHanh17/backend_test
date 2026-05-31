package com.qd.service;

import java.util.Map;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import com.qd.pojo.Providers;

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
    void updateServiceImagesAndStatusComprehensive(String username, Long serviceId, List<Long> retainImageIds, MultipartFile[] files, String status);

    Map<String, Object> getOrdersByProvider(String username, Map<String, String> params) ;
    Map<String, Object> getProviderOrderDetail(String username, Long orderId);
    Providers findProviderByUsername(String username);

}