/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.Providers;
import java.util.List;
import java.util.Map;


/**
 *
 * @author ADMIN
 */
public interface ProviderRepository {
    boolean isExistsByCompanyName(String companyName);
    boolean isExistsByTaxCode(String taxCode);
    boolean isExistsByHotline(String hotline);
    List<Providers>getProviders(Map<String,String>params);
    List<Providers> getProvidersByStatus(boolean isApproved, Map<String, String> params);
    void save(Providers provider);
    Long countProvidersByStatus(boolean isApproved,Map<String, String> params);
    Providers getProviderWithUserById(Long id);
    void updateProvider(Providers provider);
}
