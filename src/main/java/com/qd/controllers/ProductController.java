///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.qd.controllers;
//
//import com.qd.pojo.Product;
//import com.qd.service.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// *
// * @author ADMIN
// */
//@Controller
//@RequestMapping("/admin")
//public class ProductController {
//    @Autowired
//    private ProductService prodService;
//    
//    @GetMapping("/products")
//    public String createView(Model model){
//        model.addAttribute("product", new Product());
//        
//        return "products";
//    }
//    
//    @PostMapping("/products")
//    public String create(Model model,@ModelAttribute(value="product") Product p){
//        this.prodService.addOrUpdateProduct(p);
//       return "redirect:/";
//    }
//}
