/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

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

    public SellableItems findSellableItemById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(SellableItems.class, id);
    }

    public PaymentMethods findPaymentMethodById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(PaymentMethods.class, id);
    }

    public void saveOrder(Orders order) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(order);
    }

    public void saveOrderDetail(OrderDetails detail) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(detail);
    }

    public void updateSellableItem(SellableItems item) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(item);
    }
}
