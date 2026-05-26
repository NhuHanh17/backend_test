///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.qd.service.impl;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import com.qd.pojo.Product;
//import com.qd.repository.ProductRepository;
//import com.qd.service.ProductService;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author ADMIN
// */
//@Service
//public class ProductServiceImpl implements ProductService{
//    @Autowired
//    private ProductRepository prodRepo;
//    
//    @Autowired
//    private Cloudinary cloudinary;
//    
//    @Override
//    public List<Product> getProducts(Map<String, String> params) {
//        return this.prodRepo.getProducts(params);
//    }
//
//    @Override
//    public void addOrUpdateProduct(Product p) {
//        if(!p.getFile().isEmpty())
//        {
//            try {
//                Map res=this.cloudinary.uploader().upload(p.getFile().getBytes(), ObjectUtils.asMap("resource_type","auto"));
//                p.setImage(res.get("secure_url").toString());
//            } catch (IOException ex) {
//                Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        this.prodRepo.addOrUpdateProduct(p);
//    }
//    
//}
