/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.CartItems;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.SellableItems;

/**
 *
 * @author ADMIN
 */
public interface CheckoutRepository {
    SellableItems findSellableItemById(Long id);
    CartItems findCartItemById(Long id);
    PaymentMethods findPaymentMethodById(Long id);
    void saveOrder(Orders order);
    void saveOrderDetail(OrderDetails detail);
    void updateSellableItem(SellableItems item);
    void deleteCartItem(CartItems cartItem);  
    Orders findOrderById(Long id);
    void updateOrder(Orders order);
}
