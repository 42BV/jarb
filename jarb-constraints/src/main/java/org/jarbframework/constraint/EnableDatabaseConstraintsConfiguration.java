/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.HibernateJpaBeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.ExceptionTranslatingBeanPostProcessor;
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
    
    private static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";
    private static final String BASE_PACKAGE_REF = "basePackage";
    private static final String TRANSLATING_ANNOTATION_REF = "translatingAnnotation";

    private Map<String, Object> attributes;

    @Autowired
    private ApplicationContext applicationContext;

    private EntityManagerFactory entityManagerFactory;
    
    private DataSource dataSource;
    
    private String basePackage;

    @Bean
    public DatabaseConstraintExceptionTranslator exceptionTranslator() throws Exception {
        return new DatabaseConstraintExceptionTranslatorFactoryBean(basePackage, dataSource).getObject();
    }
    
    @Bean
    @SuppressWarnings("unchecked")
    public ExceptionTranslatingBeanPostProcessor exceptionTranslatingBeanPostProcessor() throws Exception {
        Class<? extends Annotation> annotation = (Class<? extends Annotation>) attributes.get(TRANSLATING_ANNOTATION_REF);
        return new ExceptionTranslatingBeanPostProcessor(exceptionTranslator(), annotation);
    }
    
    @Bean
    public BeanMetadataRepository beanMetadataRepository() throws Exception {
        return new HibernateJpaBeanMetadataRepositoryFactoryBean(entityManagerFactory).getObject();
    }
    
    @Bean
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        return new BeanConstraintDescriptorFactoryBean(beanMetadataRepository(), basePackage).getObject();
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        attributes = importMetadata.getAnnotationAttributes(EnableDatabaseConstraints.class.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String entityManagerFactoryRef = attributes.get(ENTITY_MANAGER_FACTORY_REF).toString();
        entityManagerFactory = applicationContext.getBean(entityManagerFactoryRef, EntityManagerFactory.class);
        dataSource = HibernateUtils.getDataSource(entityManagerFactory);
        basePackage = attributes.get(BASE_PACKAGE_REF).toString();
    }

}
