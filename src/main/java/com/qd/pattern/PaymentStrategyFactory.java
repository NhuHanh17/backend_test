/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern;

import com.qd.pojo.PaymentMethods;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component
public class PaymentStrategyFactory {
    public static PaymentStrategy getStrategy(boolean isFree, PaymentMethods method) {
        if (isFree) 
            return new FreePayment();

        String methodName = method.getMethodName().toUpperCase();
        if (methodName.contains("CASH"))     return new CashPayment();
        if (methodName.contains("MOMO"))     return new MomoPayment();
        if (methodName.contains("PAYPAL"))   return new PaypalPayment();
        if (methodName.contains("STRIPE"))   return new StripePayment();
        if (methodName.contains("ZALOPAY")) return new ZaloPayment(); 
        throw new RuntimeException("Hệ thống chưa tích hợp phương thức thanh toán " + method.getMethodName());
    }
}
