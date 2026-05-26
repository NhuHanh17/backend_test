/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.repository.impl;

import com.qd.pojo.Categories;
import com.qd.repository.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class CategoryRepositoryImpl implements CategoryRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;
    @Override
    public List<Categories> getCates(Map<String, String> params){
//        Session session=this.factory.getObject().getCurrentSession();
//        Query query=session.createQuery("FROM Category", Category.class);
//        return query.getResultList();
        Session session = this.factory.getObject().getCurrentSession();    
        CriteriaBuilder b = session.getCriteriaBuilder();

        //Khai bao rang muon tao 1 cau truy van co ket qua tra ve la cac Product
        CriteriaQuery<Categories> q = b.createQuery(Categories.class);//Từ cái root này có thể trỏ đến các thuộc tính như 
        //root.get("name") hay root.get("price").

        Root<Categories> root = q.from(Categories.class); // giong FROM Product
        q.select(root); //tương đương với SELECT *
        Query<Categories> query = session.createQuery(q);// Vác bản thiết kế 'q' nạp vào session để tạo ra câu lệnh thực thi chuẩn của Hibernate

        String pageSizeStr = this.env.getProperty("categories.page_size", "20");
        int pageSize = Integer.parseInt(pageSizeStr);
        
        // Lấy số trang khách muốn coi từ params (Ví dụ: ReactJS truyền lên ?page=2)
        int page = 1; // Mặc định nếu không truyền gì thì coi trang 1
        if (params != null && params.containsKey("page")) {
            page = Integer.parseInt(params.get("page"));
        }
        
        // Tính vị trí bắt đầu cắt hàng trong database
        int start = (page - 1) * pageSize;
        query.setMaxResults(pageSize);
        query.setFirstResult(start);
        
        return query.getResultList();
    }
}
