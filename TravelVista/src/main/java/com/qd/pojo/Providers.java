/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "providers")
@NamedQueries({
    @NamedQuery(name = "Providers.findAll", query = "SELECT p FROM Providers p"),
    @NamedQuery(name = "Providers.findById", query = "SELECT p FROM Providers p WHERE p.id = :id"),
    @NamedQuery(name = "Providers.findByCompanyName", query = "SELECT p FROM Providers p WHERE p.companyName = :companyName"),
    @NamedQuery(name = "Providers.findByTaxCode", query = "SELECT p FROM Providers p WHERE p.taxCode = :taxCode"),
    @NamedQuery(name = "Providers.findByHotline", query = "SELECT p FROM Providers p WHERE p.hotline = :hotline"),
    @NamedQuery(name = "Providers.findByBusinessAddress", query = "SELECT p FROM Providers p WHERE p.businessAddress = :businessAddress"),
    @NamedQuery(name = "Providers.findByIsApproved", query = "SELECT p FROM Providers p WHERE p.isApproved = :isApproved"),
    @NamedQuery(name = "Providers.findByApprovedAt", query = "SELECT p FROM Providers p WHERE p.approvedAt = :approvedAt")})
public class Providers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "company_name")
    private String companyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tax_code")
    private String taxCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "hotline")
    private String hotline;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "business_address")
    private String businessAddress;
    @Column(name = "is_approved")
    private Boolean isApproved;
    @Column(name = "approved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "providerId")
    private Set<Services> servicesSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "providerId")
    private Set<ProviderOrders> providerOrdersSet;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Users userId;

    public Providers() {
    }

    public Providers(Long id) {
        this.id = id;
    }

    public Providers(Long id, String companyName, String taxCode, String hotline, String businessAddress) {
        this.id = id;
        this.companyName = companyName;
        this.taxCode = taxCode;
        this.hotline = hotline;
        this.businessAddress = businessAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Date getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Date approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Set<Services> getServicesSet() {
        return servicesSet;
    }

    public void setServicesSet(Set<Services> servicesSet) {
        this.servicesSet = servicesSet;
    }

    public Set<ProviderOrders> getProviderOrdersSet() {
        return providerOrdersSet;
    }

    public void setProviderOrdersSet(Set<ProviderOrders> providerOrdersSet) {
        this.providerOrdersSet = providerOrdersSet;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof Providers)) {
            return false;
        }
        Providers other = (Providers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.qd.pojo.Providers[ id=" + id + " ]";
    }
    
}
