/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.payment;

import com.qd.enums.PaymentStatus;
import com.qd.pojo.Orders;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class FreePayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        order.setPaymentStatus(PaymentStatus.PAY);
        return Map.of(
            "paymentRequired", false,
            "redirectUrl", "",
            "message", "Đơn hàng miễn phí, tự động kích hoạt dịch vụ!"
        );
    }
}
