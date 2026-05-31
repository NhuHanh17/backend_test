package com.qd.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.qd.pojo.OrderDetails;
import com.qd.pojo.Reviews;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.repository.ReviewRepository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.Predicate;

import org.hibernate.Session;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class ReviewRepositoryImpl implements ReviewRepository{
   @Autowired
    private LocalSessionFactoryBean factory;  

    @Autowired
    private Environment env;

    @Override
    @Transactional(readOnly = true)
    public List<Reviews> getReviewsByServicePaged(Long serviceId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Reviews> q = b.createQuery(Reviews.class);
        Root<Reviews> root = q.from(Reviews.class);

        root.fetch("userId", JoinType.INNER);
        Join<Reviews, OrderDetails> joinDetail = root.join("orderDetailId", JoinType.INNER);
        Join<OrderDetails, SellableItems> joinItem = joinDetail.join("itemId", JoinType.INNER);
        Join<SellableItems, Services> joinService = joinItem.join("serviceId", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(joinService.get("id"), serviceId));

        if (params != null && params.containsKey("sellableItemId") && !params.get("sellableItemId").trim().isEmpty()) {
            Long sellableId = Long.parseLong(params.get("sellableItemId").trim());
            predicates.add(b.equal(joinItem.get("id"), sellableId));
        }

        q.select(root).distinct(true);
        q.where(predicates.toArray(new Predicate[0])); 
        q.orderBy(b.desc(root.get("createdAt"))); 

        var query = session.createQuery(q);
        String pageSizeStr = this.env.getProperty("review.page_size", "10"); 
        int pageSize = Integer.parseInt(pageSizeStr);
        int page = (params != null && params.containsKey("page")) ? Integer.parseInt(params.get("page")) : 1;
        
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsByService(Long serviceId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Reviews> root = q.from(Reviews.class);

        Join<Reviews, OrderDetails> joinDetail = root.join("orderDetailId", JoinType.INNER);
        Join<OrderDetails, SellableItems> joinItem = joinDetail.join("itemId", JoinType.INNER);
        Join<SellableItems, Services> joinService = joinItem.join("serviceId", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(joinService.get("id"), serviceId));

        if (params != null && params.containsKey("sellableItemId") && !params.get("sellableItemId").trim().isEmpty()) {
            Long sellableId = Long.parseLong(params.get("sellableItemId").trim());
            predicates.add(b.equal(joinItem.get("id"), sellableId));
        }

        q.select(b.countDistinct(root)).where(predicates.toArray(new Predicate[0])); // 🎯 Sạch rác phẳng lỳ
        return session.createQuery(q).getSingleResult();
    }


    @Override
    public OrderDetails findOrderDetailWithOrderAndUser(Long orderDetailId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> q = b.createQuery(OrderDetails.class);
        Root<OrderDetails> root = q.from(OrderDetails.class);

        Fetch<OrderDetails, com.qd.pojo.Orders> fetchOrder = root.fetch("orderId", JoinType.INNER);
        fetchOrder.fetch("userId", JoinType.INNER);

        q.select(root).where(b.equal(root.get("id"), orderDetailId));
        
        try {
            return session.createQuery(q).uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void saveReview(Reviews review) {
        this.factory.getObject().getCurrentSession().persist(review);
    }

    @Override
    public boolean hasReviewForOrderDetail(Long orderDetailId) {
        Session session = this.factory.getObject().getCurrentSession();
        var q = session.createQuery("SELECT COUNT(r) FROM Reviews r WHERE r.orderDetailId.id = :detailId", Long.class);
        q.setParameter("detailId", orderDetailId);
        return q.getSingleResult() > 0;
    }
}
