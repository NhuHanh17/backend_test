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
public class StripePayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        String mockStripeUrl = "https://checkout.stripe.com/pay/mock_session_" + order.getId();
        return Map.of(
            "paymentRequired", true,
            "redirectUrl", mockStripeUrl,
            "message", "Khởi tạo URL thanh toán cổng Stripe quốc tế thành công!"
        );
    }
}
