/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern;

import com.qd.pojo.Orders;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class ZaloPayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        String mockZaloUrl = "https://payment.zalopay.vn/gateway/mock_order_" + order.getId();
        return Map.of(
            "paymentRequired", true,
            "redirectUrl", mockZaloUrl,
            "message", "Khởi tạo URL ứng dụng ZaloPay thành công!"
        );
    }
}
