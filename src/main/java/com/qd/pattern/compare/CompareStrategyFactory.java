/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.compare;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qd.enums.ServiceType;

/**
 *
 * @author ADMIN
 */
@Component
public class CompareStrategyFactory {
    @Autowired
    private Map<String, CompareServicesStrategy> compareStrategyMap;

    public CompareServicesStrategy getStrategy(ServiceType type) {
        String beanName = type.name() + "_COMPARE_STRATEGY"; 
        CompareServicesStrategy strategy = compareStrategyMap.get(beanName);
        if (strategy == null) {
            throw new RuntimeException("Chưa tích hợp so sánh cho kiểu " + type);
        }
        return strategy;
    }
}
