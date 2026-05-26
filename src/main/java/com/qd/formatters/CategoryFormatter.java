/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.formatters;

import com.qd.pojo.Categories;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;


/**
 *
 * @author ADMIN
 */
public class CategoryFormatter implements Formatter<Categories>{

    @Override
    public String print(Categories cate, Locale locale) {
        return String.valueOf(cate.getId());
    }

    @Override
    public Categories parse(String cateId, Locale locale) throws ParseException {
        Categories c= new Categories();
        c.setId(Long.valueOf(cateId));
        return c;
    }
    
}
