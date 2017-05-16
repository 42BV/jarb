/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.ValidatorFactory;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jarbframework.init.migrate.MigratingDataSource;
import org.jarbframework.init.migrate.liquibase.LiquibaseMigrator;
import org.jarbframework.utils.orm.hibernate.ConventionImplicitNamingStrategy;
import org.jarbframework.utils.orm.hibernate.ConventionPhysicalNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableDatabaseConstraints(basePackageClasses = ConstraintsTestConfig.class)
public class ConstraintsTestConfig extends DatabaseConstraintsConfigurer {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    @Qualifier("hibernateDialect")
    private String hibernateDialect;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("org.jarbframework.constraint");
        
        Map<String, Object> jpaProperties = new HashMap<String, Object>();
        jpaProperties.put("hibernate.implicit_naming_strategy", ConventionImplicitNamingStrategy.class.getName());
        jpaProperties.put("hibernate.physical_naming_strategy", ConventionPhysicalNamingStrategy.class.getName());
        jpaProperties.put("hibernate.dialect", hibernateDialect);
        jpaProperties.put("javax.persistence.validation.factory", validator());
        
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        return entityManagerFactoryBean;
    }
    
    @Bean
    public ValidatorFactory validator() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.setValidationMessageSource(messageSource());
        return validatorFactory;
    }
    
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
    
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Configuration
    @Profile("hsqldb")
    public static class HsqlDbConfig {
        
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabase embeddedDataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
            return createMigratingDataSource(embeddedDataSource);
        }
        
        @Bean
        public String hibernateDialect() {
            return HSQLDialect.class.getName();
        }
        
    }
    
    private static DataSource createMigratingDataSource(DataSource dataSource) {
        LiquibaseMigrator migrator = new LiquibaseMigrator("src/test/resources");
        migrator.setChangeLogPath("create-schema.xml");
        return new MigratingDataSource(dataSource, migrator);
    }
    
}
