/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.service;

import com.qd.dto.customer.CheckoutRequest;
import com.qd.pojo.Users;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface CheckoutService {
    void fulfillOrderAfterPayment(Long orderId, String transactionRef);
    Map<String, Object> executeCheckout(Users buyer, CheckoutRequest req);
}
