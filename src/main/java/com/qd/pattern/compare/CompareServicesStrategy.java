/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.pattern.compare;

import java.util.Map;

import com.qd.pojo.Services;

/**
 *
 * @author ADMIN
 */
public interface CompareServicesStrategy {
    Map<String, Object> extractSpecifications(Services service);
    
}
