/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;
import com.qd.pojo.Providers;
import com.qd.pojo.Users;
import com.qd.repository.ProviderRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class ProviderRepositoryImpl implements ProviderRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public boolean isExistsByCompanyName(String companyName) {
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();      
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(b.count(root)).where(b.equal(root.get("companyName"), companyName));
        Long count = session.createQuery(q).uniqueResult();
        return count >0;
    }

    @Override
    public boolean isExistsByTaxCode(String taxCode) {
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();      
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Providers> root = q.from(Providers.class);
        q.select(b.count(root)).where(b.equal(root.get("taxCode"), taxCode));
        Long count = session.createQuery(q).uniqueResult();
        return count >0;
    }

    @Override
    public void save(Providers provider) {
        Session session = this.factory.getObject().getCurrentSession();    
        session.merge(provider);
    }
    
}