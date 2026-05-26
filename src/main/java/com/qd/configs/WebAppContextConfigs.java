package com.qd.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qd.formatters.CategoryFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
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

@Configuration
@ComponentScan(basePackages = {
        "com.qd.configs",
        "com.qd.controllers",
        "com.qd.service",
        "com.qd.repository",
        "com.qd.utils"
})
@EnableWebMvc
@EnableTransactionManagement
public class WebAppContextConfigs implements WebMvcConfigurer {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureMessageConverters(
            List<org.springframework.http.converter.HttpMessageConverter<?>> converters) {

        org.springframework.http.converter.json.MappingJackson2HttpMessageConverter jsonConverter = new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter();

        converters.add(jsonConverter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new CategoryFormatter());
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public Cloudinary cloudinary() {

        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "secure", true));
    }

    @Bean
    public io.swagger.v3.oas.models.OpenAPI customOpenAPI() {

        return new io.swagger.v3.oas.models.OpenAPI()
                .info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("VISTA TRAVEL API DOCUMENTATION")
                                .version("1.0")
                                .description("Tài liệu hệ thống API đặt dịch vụ du lịch trực tuyến VistaDBV4"));
    }
}