package com.qd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qd.enums.PaymentStatus;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.Reviews;
import com.qd.pojo.Users;
import com.qd.repository.ReviewRepository;
import com.qd.service.ReviewService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Reviews> getReviewsByServicePaged(Long serviceId, Map<String, String> params) {
        if (serviceId == null) return new java.util.ArrayList<>();
        return reviewRepository.getReviewsByServicePaged(serviceId, params);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsByService(Long serviceId, Map<String, String> params) {
        if (serviceId == null) return 0L;
        return reviewRepository.countReviewsByService(serviceId, params);
    }

    @Override
    @Transactional
    public void postUserReview(String username, Long orderDetailId, int ratingStar, String commentText) {
        if (ratingStar < 1 || ratingStar > 5) {
            throw new RuntimeException("Lỗi: Số sao đánh giá phải nằm trong khoảng từ 1 đến 5 sao!");
        }

        OrderDetails detail = reviewRepository.findOrderDetailWithOrderAndUser(orderDetailId);
        if (detail == null) {
            throw new RuntimeException("Lỗi: Chi tiết hóa đơn này không tồn tại trên sàn TravelVista!");
        }

        Orders orderCha = detail.getOrderId();
        Users userKhach = orderCha.getUserId();

        if (!userKhach.getUsername().equals(username)) {
            throw new RuntimeException("Cảnh báo bảo mật: Bạn không có quyền đánh giá dòng hóa đơn của tài khoản khác!");
        }
        if (!PaymentStatus.PAY.equals(orderCha.getPaymentStatus())) {
            throw new RuntimeException("Thao tác bị từ chối: Đơn hàng này chưa được thanh toán thành công (PAY), không được phép đánh giá!");
        }
        if (reviewRepository.hasReviewForOrderDetail(orderDetailId)) {
            throw new RuntimeException("Thao tác thất bại: Bạn đã gửi bài đánh giá chất lượng cho dòng sản phẩm này rồi!");
        }

        Reviews review = new Reviews();
        review.setOrderDetailId(detail);
        review.setUserId(userKhach);
        review.setRating(ratingStar);
        review.setComment(commentText != null ? commentText.trim() : "");
        review.setCreatedAt(new Date());

        reviewRepository.saveReview(review);
    }
    
}
