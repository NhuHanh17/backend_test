/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.Providers;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;
import com.qd.pojo.TourItemConcs;
import com.qd.pojo.TransportDetails;
import com.qd.pojo.TransportTicketItems;
import com.qd.pojo.Users;
import com.qd.repository.ProviderRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
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
@PropertySource("classpath:configs.properties")
@Transactional
public class ProviderRepositoryImpl implements ProviderRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public boolean isExistsByCompanyName(String companyName) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(b.count(root)).where(b.equal(root.get("companyName"), companyName));
        Long count = session.createQuery(q).uniqueResult();
        return count > 0;
    }

    @Override
    public boolean isExistsByTaxCode(String taxCode) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(b.count(root)).where(b.equal(root.get("taxCode"), taxCode));
        Long count = session.createQuery(q).uniqueResult();
        return count > 0;
    }

    @Override
    public void save(Providers provider) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(provider);
    }

    @Override
    public boolean isExistsByHotline(String hotline) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(b.count(root)).where(b.equal(root.get("hotline"), hotline));
        Long count = session.createQuery(q).uniqueResult();
        return count > 0;
    }

    @Override
    public List<Providers> getProvidersByStatus(boolean isApproved, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Providers> q = b.createQuery(Providers.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(root);

        Join<Providers, Users> userJoin = (Join) root.fetch("userId");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("isApproved"), isApproved));
        
        if (!isApproved) {
            String type = (params != null) ? params.get("type") : "";

            if ("rejected".equalsIgnoreCase(type)) {
                predicates.add(b.equal(userJoin.get("isActive"), true));
                predicates.add(b.isNotNull(root.get("statusReason"))); // 👑 REASON != NULL
            } else {
                predicates.add(b.equal(userJoin.get("isActive"), true));
                predicates.add(b.isNull(root.get("statusReason")));    // REASON == NULL
            }
        } else predicates.add(b.equal(userJoin.get("isActive"), true));
        
        q.where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(userJoin.get("createdAt")));
        Query<Providers> query = session.createQuery(q);

        if (params != null) {
            int pageSize = this.env.getProperty("providers.page_size", Integer.class, 20);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));

            // Công thức Offset, page 1 lấy từ dòng 0, page 2 từ 20
            int start = (page - 1) * pageSize;
            query.setMaxResults(pageSize); // GánLIMIT cho MySQL
            query.setFirstResult(start); // Gán OFFSET
        }

        return query.getResultList();
    }

    @Override
    public Long countProvidersByStatus(boolean isApproved,Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);

        q.select(b.count(root));
        Join<Providers, Users> userJoin = root.join("userId");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("isApproved"), isApproved));

        if (!isApproved) {
            String type = (params != null) ? params.get("type") : "";
            
            if ("rejected".equalsIgnoreCase(type)) {
                predicates.add(b.equal(userJoin.get("isActive"), true));
                predicates.add(b.isNotNull(root.get("statusReason"))); // Đếm cho hội Rejected
            } else {
                predicates.add(b.equal(userJoin.get("isActive"), true));
                predicates.add(b.isNull(root.get("statusReason")));    // Đếm cho hội Pending gốc
            }
        } else predicates.add(b.equal(userJoin.get("isActive"), true));

        q.where(predicates.toArray(Predicate[]::new));
        Query<Long> query = session.createQuery(q);
        return query.uniqueResult();
    }

    @Override
    public List<Providers> getProviders(Map<String, String> params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from                                                                  // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Providers getProviderWithUserById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Providers> q = b.createQuery(Providers.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(root);
        root.fetch("userId");

        q.where(b.equal(root.get("id"), id));
        Query<Providers> query = session.createQuery(q);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null; // Không tìm thấy đối tác với ID này
        }
    }

    @Override
    public void updateProvider(Providers provider) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(provider);
    }

    @Override
    public List<Services> getProviderServicesList(Long providerId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Services> q = b.createQuery(Services.class);
        Root<Services> root = q.from(Services.class);
        q.select(root).distinct(true); 

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("providerId").get("id"), providerId));

        if (params != null && params.get("serviceType") != null) {
            String typeStr = params.get("serviceType").toUpperCase();
            predicates.add(b.equal(root.get("serviceType"), ServiceType.valueOf(typeStr)));
        }

        if (params != null && params.get("status") != null) {
            String statusStr = params.get("status").toUpperCase();
            predicates.add(b.equal(root.get("status"), ServiceStatus.valueOf(statusStr)));
        }

        if (params != null && params.get("categoryId") != null) {
            Long catId = Long.parseLong(params.get("categoryId"));
            Join<Services, Categories> catJoin = root.join("categoriesSet");
            predicates.add(b.equal(catJoin.get("id"), catId));
        }

        q.where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(root.get("createdAt")));
        Query<Services> query = session.createQuery(q);

        if (params != null) {
            int pageSize = this.env.getProperty("services.page_size", Integer.class, 10);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            query.setMaxResults(pageSize).setFirstResult((page - 1) * pageSize);
        }
        return query.getResultList();
    }

    @Override
    public Long countProviderServices(Long providerId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Services> root = q.from(Services.class);
        q.select(b.countDistinct(root));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("providerId").get("id"), providerId));

        if (params != null && params.get("serviceType") != null) {
            predicates.add(b.equal(root.get("serviceType"), ServiceType.valueOf(params.get("serviceType").toUpperCase())));
        }
        if (params != null && params.get("status") != null) {
            predicates.add(b.equal(root.get("status"), ServiceStatus.valueOf(params.get("status").toUpperCase())));
        }
        if (params != null && params.get("categoryId") != null) {
            Long catId = Long.parseLong(params.get("categoryId"));
            Join<Services, Categories> catJoin = root.join("categoriesSet");
            predicates.add(b.equal(catJoin.get("id"), catId));
        }

        q.where(predicates.toArray(Predicate[]::new));
        return session.createQuery(q).uniqueResult();
    }

    @Override
    public Services getServiceDetailByIdAndType(Long serviceId, ServiceType type) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Services> q = b.createQuery(Services.class);
        Root<Services> root = q.from(Services.class);
        q.select(root);

        if (type == ServiceType.TOUR) {
            Fetch<Services, TourDetails> tourFetch = root.fetch("tourDetails", JoinType.LEFT);
            tourFetch.fetch("tourItemConcsSet", JoinType.LEFT);
        } else if (type == ServiceType.HOTEL) {
            Fetch<Services, HotelDetails> hotelFetch = root.fetch("hotelDetails", JoinType.LEFT);
            hotelFetch.fetch("hotelRoomItemsSet", JoinType.LEFT);
        } else if (type == ServiceType.TRANSPORT) {
            Fetch<Services,TransportDetails> transportFetch = root.fetch("transportDetails", JoinType.LEFT);
            transportFetch.fetch("transportTicketItemsSet", JoinType.LEFT);
        }

        q.where(b.and(
            b.equal(root.get("id"), serviceId),
            b.equal(root.get("serviceType"), type)
        ));

        try { return session.createQuery(q).getSingleResult(); } catch (Exception e) { return null; }
    }

    @Override
    public void saveService(Services service) {
        Session session = this.factory.getObject().getCurrentSession();
        if (service.getId() == null) {
            session.persist(service); 
            session.merge(service); 
        }
    }

    @Override
    public void saveTourDetails(TourDetails tourDetails) {
        Session session = this.factory.getObject().getCurrentSession();
        if (tourDetails.getServiceId() == null) {
            session.persist(tourDetails);
        } else {
            session.merge(tourDetails);
        }
    }

    @Override
    public void saveHotelDetails(HotelDetails hotelDetails) {
        Session session = this.factory.getObject().getCurrentSession();
        if (hotelDetails.getServiceId() == null) {
            session.persist(hotelDetails);
        } else {
            session.merge(hotelDetails);
        }
    }

    @Override
    public void saveTransportDetails(TransportDetails transportDetails) {
        Session session = this.factory.getObject().getCurrentSession();
        if (transportDetails.getServiceId() == null) {
            session.persist(transportDetails);
        } else {
            session.merge(transportDetails);
        }
    }

    @Override
    public void saveTourSchedule(TourItemConcs schedule) {
        Session session = this.factory.getObject().getCurrentSession();
        if (schedule.getId() == null) {
            session.persist(schedule);
        } else {
            session.merge(schedule);
        }
    }

    @Override
    public void saveHotelRoomItem(HotelRoomItems roomItem) {
        Session session = this.factory.getObject().getCurrentSession();
        if (roomItem.getId() == null) {
            session.persist(roomItem);
        } else {
            session.merge(roomItem);
        }
    }

    @Override
    public void saveTransportTicketItem(TransportTicketItems ticketItem) {
        Session session = this.factory.getObject().getCurrentSession();
        if (ticketItem.getId() == null) {
            session.persist(ticketItem);
        } else {
            session.merge(ticketItem);
        }
    }

    @Override
    public void saveSellableItem(SellableItems sellItem) {
        Session session = this.factory.getObject().getCurrentSession();
        if (sellItem.getId() == null) {
            session.persist(sellItem);
        } else {
            session.merge(sellItem);
        }
    }

    @Override
    public Categories getCategoryById(Long id) {
        return this.factory.getObject().getCurrentSession().get(Categories.class, id);
    }

    @Override
    public void saveServiceImage(ServiceImages img) {
        this.factory.getObject().getCurrentSession().merge(img);
}

}