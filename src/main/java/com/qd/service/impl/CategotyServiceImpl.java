/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;

import com.qd.pojo.Categories;

import com.qd.repository.CategoryRepository;
import com.qd.service.CategoryService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
public class CategotyServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCates(Map<String, String> params) {
        
        List<Categories> categories = categoryRepository.getCates(params);
        return categories.stream().map(cate -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cate.getId());
            map.put("name", cate.getName());
            map.put("serviceType", cate.getServiceType() != null ? cate.getServiceType().toString() : null);
            if (cate.getParentId() != null) {
                map.put("parentId", cate.getParentId().getId());
                map.put("parentName", cate.getParentId().getName());
            } else {
                map.put("parentId", null);
                map.put("parentName", null);
            }
            return map;
        }).collect(Collectors.toList());
    }
    
    
}
