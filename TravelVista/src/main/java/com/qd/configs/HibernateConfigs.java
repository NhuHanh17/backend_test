package com.qd.configs;

import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
        "com.qd.configs",
        "com.qd.repository",
        "com.qd.service",
        "com.qd.utils"
})
public class HibernateConfigs {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(
                env.getProperty("db.driver"));

        dataSource.setUrl(
                env.getProperty("db.url"));

        dataSource.setUsername(
                env.getProperty("db.username"));

        dataSource.setPassword(
                env.getProperty("db.password"));

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(dataSource());

        sessionFactory.setPackagesToScan(
                "com.qd.pojo");

        sessionFactory.setHibernateProperties(
                hibernateProperties());

        return sessionFactory;
    }

    private Properties hibernateProperties() {

        Properties props = new Properties();

        props.put(
                "hibernate.dialect",
                env.getProperty("hibernate.dialect"));

        props.put(
                "hibernate.show_sql",
                env.getProperty("hibernate.show_sql"));

        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {

        HibernateTransactionManager txManager = new HibernateTransactionManager();

        txManager.setSessionFactory(
                getSessionFactory().getObject());

        return txManager;
    }
}