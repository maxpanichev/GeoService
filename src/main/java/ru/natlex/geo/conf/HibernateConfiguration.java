package ru.natlex.geo.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("file:../conf/geo.properties")
public class HibernateConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
                new String[] { "ru.natlex.geo.entity" });
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        System.out.println("connect to url: " + env.getProperty("url"));
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("driver"))
                .url(env.getProperty("url"))
                .username(env.getProperty("user"))
                .password(env.getProperty("password"))
                .build();
    }


    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        //hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return hibernateProperties;
    }


}