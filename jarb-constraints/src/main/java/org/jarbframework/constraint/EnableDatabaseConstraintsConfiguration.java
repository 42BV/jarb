/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.HibernateJpaBeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.ExceptionTranslatingBeanPostProcessor;
import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.ConfigurableViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.utils.orm.hibernate.HibernateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Configuration that constructs all beans for handling database constraints.
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
@Configuration
public class EnableDatabaseConstraintsConfiguration implements ImportAware, InitializingBean {
    
    // Meta-data constants

    private static final String BASE_PACKAGE_REF = "basePackage";
    private static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";
    private static final String TRANSLATING_ANNOTATION_REF = "translatingAnnotation";

    private Map<String, Object> metadata;
    
    @Autowired(required = false)
    private Set<EnableDatabaseConstraintsConfigurer> configurers = new HashSet<>();

    @Autowired
    private ApplicationContext applicationContext;

    private EntityManagerFactory entityManagerFactory;
    
    private DataSource dataSource;
    
    //
    // Register the required beans
    //
    
    @Bean
    public DatabaseConstraintViolationResolver violationResolver() {
        String basePackage = metadata.get(BASE_PACKAGE_REF).toString();
        ConfigurableViolationResolver violationResolver = new ConfigurableViolationResolver(dataSource, basePackage);
        for (EnableDatabaseConstraintsConfigurer configurer : configurers) {
            configurer.registerResolvers(violationResolver);
        }
        return violationResolver;
    }
    
    @Bean
    public DatabaseConstraintExceptionFactory exceptionFactory() {
        String basePackage = metadata.get(BASE_PACKAGE_REF).toString();
        ConfigurableConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory();
        for (EnableDatabaseConstraintsConfigurer configurer : configurers) {
            configurer.registerExceptions(exceptionFactory);
        }
        return exceptionFactory.registerAll(basePackage);
    }
    
    @Bean
    public DatabaseConstraintExceptionTranslator exceptionTranslator() throws Exception {
        return new DatabaseConstraintExceptionTranslator(violationResolver(), exceptionFactory());
    }
    
    @Bean
    @SuppressWarnings("unchecked")
    public ExceptionTranslatingBeanPostProcessor exceptionTranslatingBeanPostProcessor() throws Exception {
        Class<? extends Annotation> annotation = (Class<? extends Annotation>) metadata.get(TRANSLATING_ANNOTATION_REF);
        return new ExceptionTranslatingBeanPostProcessor(exceptionTranslator(), annotation);
    }
    
    @Bean
    public BeanMetadataRepository beanMetadataRepository() throws Exception {
        return new HibernateJpaBeanMetadataRepositoryFactoryBean(entityManagerFactory).getObject();
    }
    
    @Bean
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        BeanConstraintDescriptor beanConstraintDescriptor = new BeanConstraintDescriptorFactoryBean(beanMetadataRepository()).getObject();
        for (EnableDatabaseConstraintsConfigurer configurer : configurers) {
            configurer.registerPropertyEnhancers(beanConstraintDescriptor);
        }
        return beanConstraintDescriptor;
    }
    
    //
    // Extract meta-data from @EnableDatabaseConstraints
    //

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        metadata = importMetadata.getAnnotationAttributes(EnableDatabaseConstraints.class.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String entityManagerFactoryRef = metadata.get(ENTITY_MANAGER_FACTORY_REF).toString();
        entityManagerFactory = applicationContext.getBean(entityManagerFactoryRef, EntityManagerFactory.class);
        dataSource = HibernateUtils.getDataSource(entityManagerFactory);
    }

}
