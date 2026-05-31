package com.qd.service;

import java.util.Map;

public interface CustomerService {

    Map<String, Object> getServicesForCustomer(Map<String, String> params);
    Map<String, Object> getServiceMainDetail(Long id);
    Map<String, Object> getServiceSubItems(Long id);
}
