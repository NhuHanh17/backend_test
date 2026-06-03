/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.payment;

import com.qd.pojo.Orders;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class MomoPayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        String mockMomoUrl = "https://payment.momo.vn/gateway/api?orderId=" + order.getId();
        return Map.of("paymentRequired", true, "redirectUrl", mockMomoUrl, "message", "Khởi tạo URL thanh toán MoMo thành công!");
    }
}
