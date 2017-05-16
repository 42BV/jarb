/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jarbframework.utils.orm.hibernate.ConventionImplicitNamingStrategy;
import org.jarbframework.utils.orm.hibernate.ConventionPhysicalNamingStrategy;
import org.jarbframework.utils.orm.hibernate.dialect.ImprovedHsqlDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Simple configuration with Hibernate.
 * 
 * @author Jeroen van Schagen
 * @since Jul 8, 2014
 */
@Configuration
@Import(UtilsTestConfig.class)
public class HibernateConfig {
    
    @Autowired
    private DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("org.jarbframework.utils.domain");
        
        Map<String, Object> jpaProperties = new HashMap<String, Object>();
        jpaProperties.put("hibernate.implicit_naming_strategy", ConventionImplicitNamingStrategy.class.getName());
        jpaProperties.put("hibernate.physical_naming_strategy", ConventionPhysicalNamingStrategy.class.getName());
        jpaProperties.put("hibernate.dialect", ImprovedHsqlDialect.class.getName());
        jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
        
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        return entityManagerFactoryBean;
    }

}
