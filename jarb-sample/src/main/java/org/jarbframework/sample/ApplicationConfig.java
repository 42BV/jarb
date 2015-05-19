/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.validator.constraints.Email;
import org.jarbframework.constraint.DatabaseConstraintsConfigurer;
import org.jarbframework.constraint.EnableDatabaseConstraints;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.jarbframework.init.migrate.liquibase.LiquibaseMigratingDatabaseBuilder;
import org.jarbframework.init.populate.SqlDatabasePopulator;
import org.jarbframework.init.populate.listener.PopulateApplicationListener;
import org.jarbframework.init.populate.listener.PopulateApplicationListenerBuilder;
import org.jarbframework.sample.populator.PostPopulator;
import org.jarbframework.utils.orm.hibernate.ConventionNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.jarbframework.sample")
@EnableDatabaseConstraints(basePackages = "org.jarbframework.sample")
@ComponentScan(basePackages = "org.jarbframework.sample", 
               excludeFilters = { @Filter(RestController.class), @Filter(Controller.class), @Filter(ControllerAdvice.class) })
public class ApplicationConfig extends DatabaseConstraintsConfigurer {
    
    @Bean
    public DataSource dataSource() {
        return new LiquibaseMigratingDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("org.jarbframework.sample");
        
        Map<String, Object> jpaProperties = new HashMap<String, Object>();
        jpaProperties.put("hibernate.ejb.naming_strategy", ConventionNamingStrategy.class.getName());
        jpaProperties.put("hibernate.dialect", HSQLDialect.class.getName());
        jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
        jpaProperties.put("hibernate.jdbc.use_get_generated_keys", true);
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

    @Override
    public void configureConstraintDescriptor(BeanConstraintDescriptor beanConstraintDescriptor) {
        beanConstraintDescriptor.register(new AnnotationPropertyTypeEnhancer(Email.class, "my-email"));
    }

    @Profile("demo")
    @Configuration
    public static class DemoConfig {
        
        @Autowired
        private DataSource dataSource;
        
        @Autowired
        private EntityManagerFactory entityManagerFactory;
        
        @Bean
        public PopulateApplicationListener populateAppliationListener() {
            return new PopulateApplicationListenerBuilder()
                            .initializer()
                                .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("import.sql")))
                                .add(postPopulator())
                                .and()
                            .destroyer()
                                .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("clean.sql")))
                                .build();
        }
        
        @Bean
        public PostPopulator postPopulator() {
            return new PostPopulator();
        }

    }

}