/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import com.qd.enums.ServiceType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "categories")
@NamedQueries({
    @NamedQuery(name = "Categories.findAll", query = "SELECT c FROM Categories c"),
    @NamedQuery(name = "Categories.findById", query = "SELECT c FROM Categories c WHERE c.id = :id"),
    @NamedQuery(name = "Categories.findByName", query = "SELECT c FROM Categories c WHERE c.name = :name"),
    @NamedQuery(name = "Categories.findByServiceType", query = "SELECT c FROM Categories c WHERE c.serviceType = :serviceType")})
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type",nullable = false)
    private ServiceType serviceType;
    @JoinTable(name = "service_categories", joinColumns = {
        @JoinColumn(name = "category_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "service_id", referencedColumnName = "id")})
    @ManyToMany
    private Set<Services> servicesSet;
    @OneToMany(mappedBy = "parentId")
    private Set<Categories> categoriesSet;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private Categories parentId;

    public Categories() {
    }

    public Categories(Long id) {
        this.id = id;
    }

    public Categories(Long id, String name, ServiceType serviceType) {
        this.id = id;
        this.name = name;
        this.serviceType = serviceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Services> getServicesSet() {
        return servicesSet;
    }

    public void setServicesSet(Set<Services> servicesSet) {
        this.servicesSet = servicesSet;
    }

    public Set<Categories> getCategoriesSet() {
        return categoriesSet;
    }

    public void setCategoriesSet(Set<Categories> categoriesSet) {
        this.categoriesSet = categoriesSet;
    }

    public Categories getParentId() {
        return parentId;
    }

    public void setParentId(Categories parentId) {
        this.parentId = parentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categories)) {
            return false;
        }
        Categories other = (Categories) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.Categories[ id=" + id + " ]";
    }

    /**
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
}
