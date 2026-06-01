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
public class PaypalPayment implements PaymentStrategy{
    @Override
    public Map<String, Object> processPayment(Orders order) {
        String mockPaypalUrl = "https://www.paypal.com/checkout?orderId=" + order.getId();
        return Map.of("paymentRequired", true, "redirectUrl", mockPaypalUrl, "message", "Khởi tạo URL thanh toán PayPal thành công!");
    }
}
