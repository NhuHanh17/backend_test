/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.pojo.CartItems;
import com.qd.pojo.Carts;
import com.qd.pojo.Categories;
import com.qd.pojo.Orders;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.repository.CartRepository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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

public class CartRepositoryImpl implements CartRepository{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public void saveCart(Carts cart) {
        Session session = this.factory.getObject().getCurrentSession();   
        session.persist(cart); 
    }

    @Override
    @Transactional(readOnly = true)
    public Carts findCartByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Carts> q = b.createQuery(Carts.class);
        Root<Carts> root = q.from(Carts.class);

        root.fetch("userId", JoinType.INNER);
        q.select(root).where(b.equal(root.get("userId").get("username"), username.trim()));

        try {
            Query<Carts> query = session.createQuery(q);
            return query.uniqueResult();
        } catch (Exception e) {
            return null;
        }
}

    @Override
    @Transactional(readOnly = true)
    public List<CartItems> getCartItemsPaged(Long cartId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<CartItems> q = b.createQuery(CartItems.class);
        Root<CartItems> root = q.from(CartItems.class);

        Fetch<CartItems, SellableItems> fetchSellable = root.fetch("itemId", JoinType.INNER);
        Fetch<SellableItems, Services> fetchService = fetchSellable.fetch("serviceId", JoinType.INNER);
        fetchService.fetch("serviceImagesSet", JoinType.LEFT);
        fetchService.fetch("providerId", JoinType.INNER);

        q.select(root).where(b.equal(root.get("cartId").get("id"), cartId));
        q.orderBy(b.desc(root.get("createdAt")));
        Query<CartItems> query = session.createQuery(q);
        String pageSizeStr = this.env.getProperty("cart.page_size", "15");
        int pageSize = Integer.parseInt(pageSizeStr);
        int page = (params != null && params.containsKey("page")) ? Integer.parseInt(params.get("page")) : 1;

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCartItems(Long cartId) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(ci) FROM CartItems ci WHERE ci.cartId.id = :cartId", Long.class);
        query.setParameter("cartId", cartId);
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public CartItems findCartItemByCartAndSellableItem(Long cartId, Long sellableItemId) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<CartItems> query = session.createQuery("SELECT ci FROM CartItems ci WHERE ci.cartId.id = :cartId AND ci.itemId.id = :itemId", CartItems.class);
        query.setParameter("cartId", cartId);
        query.setParameter("itemId", sellableItemId);
        return query.uniqueResult();
    }

    @Override
    public void saveCartItem(CartItems cartItem) {
        this.factory.getObject().getCurrentSession().persist(cartItem);
    }

    @Override
    public void updateCartItem(CartItems cartItem) {
        this.factory.getObject().getCurrentSession().merge(cartItem);
    }

    @Override
    public void deleteCartItem(CartItems cartItem) {
       if (cartItem == null || cartItem.getId() == null) return;
        Session session = this.factory.getObject().getCurrentSession();
        CartItems managedItem = session.contains(cartItem) ? cartItem : (CartItems) session.merge(cartItem);       
        session.remove(managedItem);
        session.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public CartItems findCartItemById(Long id) {
        return this.factory.getObject().getCurrentSession().find(CartItems.class, id);
    }
    @Override
    public SellableItems findSellableItemForUpdate(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.find(SellableItems.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public void updateSellableItem(SellableItems item) {
        this.factory.getObject().getCurrentSession().merge(item); 
    }

    @Override
    public void createOrder(Orders order) {
        this.factory.getObject().getCurrentSession().persist(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Categories getCategoryById(Long id) {
    return this.factory.getObject().getCurrentSession().find(Categories.class,id);    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItems> findCartItemsForPreview(List<Long> cartItemIds) {        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        
        CriteriaQuery<CartItems> q = b.createQuery(CartItems.class);
        Root<CartItems> rootCart = q.from(CartItems.class);

        Fetch<CartItems, SellableItems> fetchSellable = rootCart.fetch("itemId", JoinType.INNER);
        Fetch<SellableItems, Services> fetchService = fetchSellable.fetch("serviceId", JoinType.INNER);
        fetchService.fetch("providerId", JoinType.INNER);
        fetchService.fetch("serviceImagesSet", JoinType.LEFT); 

        q.select(rootCart).distinct(true)
         .where(rootCart.get("id").in(cartItemIds));

        Query<CartItems> query = session.createQuery(q);
        return query.getResultList();
    }

}
