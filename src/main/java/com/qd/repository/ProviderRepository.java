/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import com.qd.pojo.Providers;

/**
 *
 * @author ADMIN
 */
public interface ProviderRepository {
    boolean isExistsByCompanyName(String companyName);
    boolean isExistsByTaxCode(String taxCode);
    void save(Providers provider);
}
