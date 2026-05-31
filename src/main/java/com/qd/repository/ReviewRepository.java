/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.OrderDetails;
import com.qd.pojo.Reviews;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface ReviewRepository {
    List<Reviews> getReviewsByServicePaged(Long serviceId, Map<String, String> params);
    Long countReviewsByService(Long serviceId, Map<String, String> params);
    
    OrderDetails findOrderDetailWithOrderAndUser(Long orderDetailId);
    void saveReview(Reviews review);
    boolean hasReviewForOrderDetail(Long orderDetailId);
}
