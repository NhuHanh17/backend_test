/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.pojo.CartItems;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.SellableItems;
import com.qd.repository.CheckoutRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class CheckoutRepositoryImpl implements CheckoutRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public SellableItems findSellableItemById(Long id) {
        return this.factory.getObject().getCurrentSession().get(SellableItems.class, id);
    }

    @Override
    public CartItems findCartItemById(Long id) {
        return this.factory.getObject().getCurrentSession().get(CartItems.class, id);
    }
    
    @Override
    public PaymentMethods findPaymentMethodById(Long id) {
        return this.factory.getObject().getCurrentSession().get(PaymentMethods.class, id);
    }
    
    @Override
    public void saveOrder(Orders order) {
        this.factory.getObject().getCurrentSession().persist(order);
    }
    
    @Override
    public void saveOrderDetail(OrderDetails detail) {
        this.factory.getObject().getCurrentSession().persist(detail);
    }

    @Override
    public void updateSellableItem(SellableItems item) {
        this.factory.getObject().getCurrentSession().merge(item);
    }

    @Override
    public void deleteCartItem(CartItems cartItem) {
        this.factory.getObject().getCurrentSession().remove(cartItem);
    }

    @Override
    public Orders findOrderById(Long id) {
    return this.factory.getObject().getCurrentSession().get(Orders.class, id);
}
    @Override
    public void updateOrder(Orders order) {
        this.factory.getObject().getCurrentSession().merge(order);
    }
}
