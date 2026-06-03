///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
// */
package com.qd.repository;
//

import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.Services;
import java.util.List;
import java.util.Map;


public interface CustomerRepository {
    List<Services> getServicesForCustomer(Map<String, String> params);
    Long countServices(Map<String, String> params);
    List<PaymentMethods> getActivePaymentMethods();
    List<Orders> getCustomerOrdersHistory(String username,Map<String, String> params);
    Long countCustomerOrders(String username) ;
    List<Services> getServicesForComparison(List<Long> ids);
}
