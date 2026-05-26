///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
// */
package com.qd.repository;
//
import com.qd.pojo.Categories;
import java.util.List;
import java.util.Map;


public interface CategoryRepository {
    List<Categories> getCates(Map<String, String> params);
}
