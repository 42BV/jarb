/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.HibernateJpaBeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.TranslateExceptionsBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class EnableDatabaseConstraintsConfiguration implements ImportAware {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    private String basePackage;
    
    @Bean
    public DatabaseConstraintExceptionTranslator exceptionTranslator() throws Exception {
        return new DatabaseConstraintExceptionTranslatorFactoryBean(basePackage, dataSource).getObject();
    }
    
    @Bean
    public TranslateExceptionsBeanPostProcessor translateExceptionBeanPostProcessor() throws Exception {
        return new TranslateExceptionsBeanPostProcessor(exceptionTranslator());
    }
    
    @Bean
    public BeanMetadataRepository beanMetadataRepository() throws Exception {
        return new HibernateJpaBeanMetadataRepositoryFactoryBean(entityManagerFactory).getObject();
    }
    
    @Bean
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        return new BeanConstraintDescriptorFactoryBean(beanMetadataRepository()).getObject();
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(EnableDatabaseConstraints.class.getName());
        basePackage = attributes.get("basePackage").toString();
    }

}
