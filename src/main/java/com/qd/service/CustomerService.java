package com.qd.service;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    Map<String, Object> getServicesForCustomer(Map<String, String> params);
    Map<String, Object> getServiceMainDetail(Long id, String currentUsername);
    Map<String, Object> getServiceSubItems(Long id);
    List<Map<String, Object>> getPaymentMethodsForCheckout();
    Map<String, Object> getMyOrdersHistory(String username, Map<String, String> params);
    List<Map<String, Object>> compareServices(List<Long> ids);
}
