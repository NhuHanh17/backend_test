/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qd.formatters.CategoryFormatter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author ADMIN
 */
@Configuration
@ComponentScan(
        basePackages={
            "com.qd.configs",
            "com.qd.controllers",
            "com.qd.service",
            "com.qd.repository",
            "com.qd.utils"
        }
)
@EnableWebMvc
@EnableTransactionManagement
public class WebAppContextConfigs implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        WebMvcConfigurer.super.configureDefaultServletHandling(configurer); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        configurer.enable();
    }
    
    @Override
    public void configureMessageConverters(List<org.springframework.http.converter.HttpMessageConverter<?>> converters) {
        // Kích hoạt JSON của Jackson
        org.springframework.http.converter.json.MappingJackson2HttpMessageConverter jsonConverter = 
                new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter();
        // ĐkýJackson vào Spring MVC
        converters.add(jsonConverter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new CategoryFormatter());
    } 
    
    @Bean 
    public MultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }
    
    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dgq228v63",
                "api_key" , "835254455382447",
			"api_secret" , "apF9fdjvhtVl-qug96I7HJG7Fak",
			"secure" , true));
	return cloudinary;

    }
    
    @Bean
    public io.swagger.v3.oas.models.OpenAPI customOpenAPI() {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("VISTA TRAVEL API DOCUMENTATION")
                        .version("1.0")
                        .description("Tài liệu hệ thống API đặt dịch vụ du lịch trực tuyến VistaDBV4"));
    }

    
}
