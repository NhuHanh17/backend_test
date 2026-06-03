package com.qd.repository.impl;

import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.repository.CustomerRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class CustomerRepositoryImpl implements CustomerRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public List<Services> getServicesForCustomer(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Services> q = b.createQuery(Services.class);
        Root<Services> root = q.from(Services.class);
        
        root.fetch("categoriesSet", JoinType.LEFT);
        root.fetch("serviceImagesSet", JoinType.LEFT);
        root.fetch("providerId", JoinType.INNER);
        
        List<Predicate> predicates = new ArrayList<>();
                predicates.add(b.equal(root.get("status"),ServiceStatus.ACTIVATE));
        if (params != null) {
            if (params.containsKey("name") && !params.get("name").trim().isEmpty()) {
                predicates.add(b.like(root.get("name").as(String.class), "%" + params.get("name").trim() + "%"));
            }
            if (params.containsKey("serviceType") && !params.get("serviceType").trim().isEmpty()) {
                predicates.add(b.equal(root.get("serviceType"), ServiceType.valueOf(params.get("serviceType").toUpperCase())));
            }

            if (params.containsKey("cateId") && !params.get("cateId").trim().isEmpty()) {
                try {
                    Long cateId = Long.parseLong(params.get("cateId").trim());
                    Join<Services, Categories> joinCate = root.join("categoriesSet", JoinType.LEFT);
                    predicates.add(b.equal(joinCate.get("id"), cateId));
                } catch (NumberFormatException e) {
                }
            }
            if (params.containsKey("location") && !params.get("location").trim().isEmpty()) {
                String locKw = "%" + params.get("location").trim().toLowerCase() + "%";
                String sType = params.containsKey("serviceType") ? params.get("serviceType").trim().toUpperCase() : "";
                
                if ("HOTEL".equals(sType)) {
                    Join<Object, Object> joinHotel = root.join("hotelDetails", JoinType.INNER);
                    predicates.add(b.like(b.lower(joinHotel.get("city").as(String.class)), locKw));
                    
                } else if ("TOUR".equals(sType)) {
                    Join<Object, Object> joinTour = root.join("tourDetails", JoinType.INNER);
                    predicates.add(b.like(b.lower(joinTour.get("destinationLocation").as(String.class)), locKw));
                    
                } else if ("TRANSPORT".equals(sType)) {
                    Join<Object, Object> joinTrans = root.join("transportDetails", JoinType.INNER);
                    Predicate depLoc = b.like(b.lower(joinTrans.get("departureLocation").as(String.class)), locKw);
                    Predicate arrLoc = b.like(b.lower(joinTrans.get("arrivalLocation").as(String.class)), locKw);
                    predicates.add(b.or(depLoc, arrLoc));
                } else {
                    Join<Object, Object> joinHotel = root.join("hotelDetails", JoinType.LEFT);
                    Join<Object, Object> joinTour = root.join("tourDetails", JoinType.LEFT);
                    Join<Object, Object> joinTrans = root.join("transportDetails", JoinType.LEFT);
                    
                    Predicate hotelLoc = b.and(b.isNotNull(joinHotel.get("city")), b.like(b.lower(joinHotel.get("city").as(String.class)), locKw));
                    Predicate tourLoc = b.and(b.isNotNull(joinTour.get("destinationLocation")), b.like(b.lower(joinTour.get("destinationLocation").as(String.class)), locKw));
                    Predicate transLoc = b.and(b.isNotNull(joinTrans.get("arrivalLocation")), b.like(b.lower(joinTrans.get("arrivalLocation").as(String.class)), locKw));
                    
                    predicates.add(b.or(hotelLoc, tourLoc, transLoc));
                }
            }
            if ((params.containsKey("minPrice") && !params.get("minPrice").trim().isEmpty()) ||
                (params.containsKey("maxPrice") && !params.get("maxPrice").trim().isEmpty())) {
                
                Join<Services, SellableItems> joinItem = root.join("sellableItemsSet", JoinType.INNER);
                
                if (params.containsKey("minPrice") && !params.get("minPrice").trim().isEmpty()) {
                    predicates.add(b.greaterThanOrEqualTo(joinItem.get("price"), new BigDecimal(params.get("minPrice"))));
                }
                if (params.containsKey("maxPrice") && !params.get("maxPrice").trim().isEmpty()) {
                    predicates.add(b.lessThanOrEqualTo(joinItem.get("price"), new BigDecimal(params.get("maxPrice"))));
                }
            }
        }
        q.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

        String sortBy = (params != null && params.containsKey("sortBy")) ? params.get("sortBy").trim().toLowerCase() : "newest";
        switch (sortBy) {
            case "rating": 
                q.orderBy(b.desc(root.get("averageRating")));
                break;
            case "popularity": 
                q.orderBy(b.desc(root.get("bookingCount")));
                break;
            case "newest":
            default: 
                q.orderBy(b.desc(root.get("createdAt")));
                break;
        }
        Query<Services> query = session.createQuery(q);

        String pageSizeStr = this.env.getProperty("services.page_size", "20"); 
        int pageSize = Integer.parseInt(pageSizeStr);
        int page = 1;
        if (params != null && params.containsKey("page")) {
            try {
                page = Integer.parseInt(params.get("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int start = (page - 1) * pageSize;
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        
        return query.getResultList();

    }

    @Override
    public Long countServices(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Services> root = q.from(Services.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("status"), ServiceStatus.ACTIVATE));

        if (params != null) {
            if (params.containsKey("name") && !params.get("name").trim().isEmpty()) {
                predicates.add(b.like(b.lower(root.get("name").as(String.class)), "%" + params.get("name").trim().toLowerCase() + "%"));
            }
            if (params.containsKey("serviceType") && !params.get("serviceType").trim().isEmpty()) {
                predicates.add(b.equal(root.get("serviceType"), ServiceType.valueOf(params.get("serviceType").toUpperCase())));
            }
            if (params.containsKey("cateId") && !params.get("cateId").trim().isEmpty()) {
                try {
                    Long cateId = Long.parseLong(params.get("cateId").trim());
                    Join<Services, Categories> joinCate = root.join("categoriesSet", JoinType.LEFT);
                    predicates.add(b.equal(joinCate.get("id"), cateId));
                } catch (NumberFormatException e) {}
            }
            if (params.containsKey("location") && !params.get("location").trim().isEmpty()) {
                String locKw = "%" + params.get("location").trim().toLowerCase() + "%";
                String sType = params.containsKey("serviceType") ? params.get("serviceType").trim().toUpperCase() : "";
                
                if ("HOTEL".equals(sType)) {
                    Join<Object, Object> joinHotel = root.join("hotelDetails", JoinType.INNER);
                    predicates.add(b.like(b.lower(joinHotel.get("city").as(String.class)), locKw));
                    
                } else if ("TOUR".equals(sType)) {
                    Join<Object, Object> joinTour = root.join("tourDetails", JoinType.INNER);
                    predicates.add(b.like(b.lower(joinTour.get("destinationLocation").as(String.class)), locKw));
                    
                } else if ("TRANSPORT".equals(sType)) {
                    Join<Object, Object> joinTrans = root.join("transportDetails", JoinType.INNER);
                    Predicate depLoc = b.like(b.lower(joinTrans.get("departureLocation").as(String.class)), locKw);
                    Predicate arrLoc = b.like(b.lower(joinTrans.get("arrivalLocation").as(String.class)), locKw);
                    predicates.add(b.or(depLoc, arrLoc)); 
                    
                } else {
                    Join<Object, Object> joinHotel = root.join("hotelDetails", JoinType.LEFT);
                    Join<Object, Object> joinTour = root.join("tourDetails", JoinType.LEFT);
                    Join<Object, Object> joinTrans = root.join("transportDetails", JoinType.LEFT);
                    
                    Predicate hotelLoc = b.and(b.isNotNull(joinHotel.get("city")), b.like(b.lower(joinHotel.get("city").as(String.class)), locKw));
                    Predicate tourLoc = b.and(b.isNotNull(joinTour.get("destinationLocation")), b.like(b.lower(joinTour.get("destinationLocation").as(String.class)), locKw));
                    Predicate transLoc = b.and(b.isNotNull(joinTrans.get("arrivalLocation")), b.like(b.lower(joinTrans.get("arrivalLocation").as(String.class)), locKw));
                    
                    predicates.add(b.or(hotelLoc, tourLoc, transLoc));
                }
            }
            if ((params.containsKey("minPrice") && !params.get("minPrice").trim().isEmpty()) ||
                (params.containsKey("maxPrice") && !params.get("maxPrice").trim().isEmpty())) {
                
                Join<Services, SellableItems> joinItem = root.join("sellableItemsSet", JoinType.INNER);
                
                if (params.containsKey("minPrice") && !params.get("minPrice").trim().isEmpty()) {
                    predicates.add(b.greaterThanOrEqualTo(joinItem.get("price"), new BigDecimal(params.get("minPrice"))));
                }
                if (params.containsKey("maxPrice") && !params.get("maxPrice").trim().isEmpty()) {
                    predicates.add(b.lessThanOrEqualTo(joinItem.get("price"), new BigDecimal(params.get("maxPrice"))));
                }
            }
        }

        q.select(b.countDistinct(root)).where(predicates.toArray(new Predicate[0])); 
        return session.createQuery(q).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orders> getCustomerOrdersHistory(String username,Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Orders> q = b.createQuery(Orders.class);
        Root<Orders> root = q.from(Orders.class);

        root.fetch("paymentMethodId", JoinType.INNER);
        root.fetch("providerId", JoinType.INNER);
        q.select(root).where(
            b.equal(root.get("userId").get("username"), username)
        ).orderBy(b.desc(root.get("createdAt")));
        
        Query<Orders> query = session.createQuery(q); 
        String pageSizeStr = this.env.getProperty("my_orders.page_size", "20"); 
        int pageSize = Integer.parseInt(pageSizeStr);
        int page = 1;
        
        if (params != null && params.containsKey("page")) {
            try {
                page = Integer.parseInt(params.get("page"));
            } catch (NumberFormatException e) {
                page = 1; 
            }
        }       
        int start = (page - 1) * pageSize;
        query.setFirstResult(start);
        query.setMaxResults(pageSize);      
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCustomerOrders(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Orders> root = q.from(Orders.class);
        q.select(b.count(root)).where(
            b.equal(root.get("userId").get("username"), username)
        );

        Long total = session.createQuery(q).getSingleResult();
        return total != null ? total : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethods> getActivePaymentMethods() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<PaymentMethods> q = b.createQuery(PaymentMethods.class);
        Root<PaymentMethods> root = q.from(PaymentMethods.class);

        Predicate excludeFreeId = b.notEqual(root.get("id"), 6L);
        Predicate excludeFreeName = b.notEqual(b.upper(root.get("methodName")), "FREE");
        q.select(root).where(b.and(excludeFreeId, excludeFreeName));

        return session.createQuery(q).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Services> getServicesForComparison(List<Long> ids) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Services> q = b.createQuery(Services.class);
        Root<Services> root = q.from(Services.class);

        root.fetch("providerId", JoinType.INNER);
        root.fetch("sellableItemsSet", JoinType.LEFT);
        root.fetch("serviceImagesSet", JoinType.LEFT);
        q.select(root).distinct(true).where(
            b.and(root.get("id").in(ids),b.equal(root.get("status"),ServiceStatus.ACTIVATE))
        );
        return session.createQuery(q).getResultList();
    }
}