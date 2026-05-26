///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.qd.repository.impl;
//
//import com.qd.pojo.Product;
//import com.qd.repository.ProductRepository;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Predicate;
//import jakarta.persistence.criteria.Root;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import org.hibernate.Session;
//import org.hibernate.query.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// *
// * @author ADMIN
// */
//@Repository
//@PropertySource("classpath:configs.properties")
//@Transactional
//public class ProductRepositoryImpl implements ProductRepository {
//
//    @Autowired
//    private Environment env;
//    
//    @Autowired
//    private LocalSessionFactoryBean factory;
//
//    @Override
//    public List<Product> getProducts(Map<String, String> params) {
//        Session session = this.factory.getObject().getCurrentSession();
//        CriteriaBuilder b = session.getCriteriaBuilder();
//
//        //Khai bao rang muon tao 1 cau truy van co ket qua tra ve la cac Product
//        CriteriaQuery<Product> q = b.createQuery(Product.class);//Từ cái root này có thể trỏ đến các thuộc tính như 
//        //root.get("name") hay root.get("price").
//
//        Root root = q.from(Product.class); // giong FROM Product
//        q.select(root); //tương đương với SELECT *
//
//        //truy van where
//        if (params != null) {
//            List<Predicate> predicates = new ArrayList<>();
//
//            String kw = params.get("kw");
//            if (kw != null && !kw.isEmpty()) {
//                predicates.add(b.like(root.get("name"), "%" + kw + "%"));
//            }
//
//            String fromPrice = params.get("fromPrice");
//            if (fromPrice != null && !fromPrice.isEmpty()) {
//                predicates.add(b.greaterThanOrEqualTo(root.get("price"), fromPrice));
//            }
//
//            String toPrice = params.get("toPrice");
//            if (toPrice != null && !toPrice.isEmpty()) {
//                predicates.add(b.lessThanOrEqualTo(root.get("price"), toPrice));
//            }
//
//            q.where(predicates.toArray(Predicate[]::new));
//        }
//        Query query = session.createQuery(q);
//
//        //xu ly phan trang
//        if (params != null) {
//            int pageSize=this.env.getProperty("products.page_size",Integer.class);
//            int page = Integer.parseInt(params.getOrDefault("page", "1"));
//            int start = (page - 1) * pageSize;
//
//            query.setMaxResults(pageSize);
//            query.setFirstResult(start);
//        }
//
//        return query.getResultList();
//
//    }
//
////    public Product getProductById(int id){
////        try(Session session=HibernateUtils.getFACTORY().openSession() ){
////            return session.get(Product.class, id);
////        }
////    }
////    
//    @Override
//    public void addOrUpdateProduct(Product p){
//        Session session= this.factory.getObject().getCurrentSession();
//            if(p.getId()!=null){
//                session.merge(p);
//            }else{
//                session.persist(p);
//            }
//    }
////    public void deleteProduct(int id){
////        try(Session session =HibernateUtils.getFACTORY().openSession())
////        {
////            Product p=this.getProductById(id);
////            session.remove(p);
////        }
////    }
//}
