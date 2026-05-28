package com.qd.service;

import java.util.Map;

import com.qd.dto.provider.BaseComprehensiveRequest;

public interface ProviderService {
    Map<String, Object> getMyServicesList(String username, Map<String, String> params);
    Object getMyServiceDetail(String username, Long id, String typeStr);
    Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req, org.springframework.web.multipart.MultipartFile[] files);}
