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
public class CashPayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        return Map.of(
            "paymentRequired", false,
            "redirectUrl", "",
            "message", "Khởi tạo đơn hàng thanh toán bằng Tiền mặt thành công! Vui lòng thanh toán khi nhận dịch vụ."
        );
    }
}
