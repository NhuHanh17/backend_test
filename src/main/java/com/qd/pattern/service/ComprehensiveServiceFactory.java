/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern.service;

import com.qd.enums.ServiceType;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component
public class ComprehensiveServiceFactory {
    @Autowired
    private Map<String, ComprehensiveServiceStrategy> strategyMap;

    public ComprehensiveServiceStrategy getStrategy(ServiceType type) {
        String beanName = type.name() + "_STRATEGY"; 
        ComprehensiveServiceStrategy strategy = strategyMap.get(beanName);
        if (strategy == null) {
            throw new RuntimeException("Chưa cấu hình xử lý cho phân hệ " + type);
        }
        return strategy;
    }
}
