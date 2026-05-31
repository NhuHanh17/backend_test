/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.service;

import com.qd.pojo.Reviews;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface ReviewService {
    List<Reviews> getReviewsByServicePaged(Long serviceId, Map<String, String> params);
    Long countReviewsByService(Long serviceId, Map<String, String> params);
    void postUserReview(String username, Long orderDetailId, int ratingStar, String commentText);
    
}
