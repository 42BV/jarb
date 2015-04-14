/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.DefaultBeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.TranslateAdviceAddingBeanPostProcessor;
import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.ConfigurableViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.utils.StringUtils;
import org.jarbframework.utils.bean.BeanRegistry;
import org.jarbframework.utils.bean.DefaultBeanRegistry;
import org.jarbframework.utils.orm.hibernate.HibernateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Configuration that constructs all beans for handling database constraints.
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
@Configuration
public class DatabaseConstraintsConfiguration implements ImportAware, InitializingBean {
    
    // Meta-data constants

    private static final String BASE_PACKAGES_REF = "basePackages";
    private static final String BASE_CLASSES_REF = "basePackageClasses";

    private static final String DATA_SOURCE_REF = "dataSource";
    private static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";

    private static final String PROXY_ANNOTATION_REF = "proxyAnnotation";

    private Map<String, Object> attributes;
    
    @Autowired(required = false)
    private Set<DatabaseConstraintsConfigurer> configurers = new HashSet<DatabaseConstraintsConfigurer>();

    private Set<String> basePackages;

    @Autowired
    private ApplicationContext applicationContext;

    private EntityManagerFactory entityManagerFactory;
    
    private DataSource dataSource;
    
    //
    // Exception translation
    //
    
    @Bean
    @Lazy
    public DatabaseConstraintExceptionTranslator exceptionTranslator() throws Exception {
        return new DatabaseConstraintExceptionTranslator(violationResolver(), exceptionFactory());
    }
    
    @Bean
    @Lazy
    public DatabaseConstraintViolationResolver violationResolver() {
        ConfigurableViolationResolver violationResolver = new ConfigurableViolationResolver(dataSource, basePackages);
        for (DatabaseConstraintsConfigurer configurer : configurers) {
            configurer.configureViolationResolver(violationResolver);
        }
        return violationResolver;
    }
    
    @Bean
    @Lazy
    public DatabaseConstraintExceptionFactory exceptionFactory() {
        ConfigurableConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory();
        for (DatabaseConstraintsConfigurer configurer : configurers) {
            configurer.configureExceptionFactory(exceptionFactory);
        }
        for (String basePackage : basePackages) {
            exceptionFactory.registerAll(basePackage);
        }
        return exceptionFactory;
    }
    
    @Bean
    @SuppressWarnings("unchecked")
    public TranslateAdviceAddingBeanPostProcessor translateAdviceAddingBeanPostProcessor() throws Exception {
        Class<? extends Annotation> annotation = (Class<? extends Annotation>) attributes.get(PROXY_ANNOTATION_REF);
        return new TranslateAdviceAddingBeanPostProcessor(exceptionTranslator(), annotation);
    }
    
    //
    // Bean metadata descriptions
    //

    @Bean
    @Lazy
    public BeanMetadataRepository beanMetadataRepository() throws Exception {
        if (entityManagerFactory != null) {
            return new BeanMetadataRepositoryFactoryBean(entityManagerFactory).getObject();
        } else {
            return new BeanMetadataRepositoryFactoryBean(dataSource).getObject();
        }
    }
    
    @Bean
    @Lazy
    public BeanRegistry beanRegistry() {
        DefaultBeanRegistry registry = new DefaultBeanRegistry();
        if (entityManagerFactory != null) {
            registerAllEntityTypes(registry);
        }
        return registry;
    }

    private void registerAllEntityTypes(DefaultBeanRegistry registry) {
        Set<EntityType<?>> entities = entityManagerFactory.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            registry.register(entity.getBindableJavaType());
        }
    }

    @Bean
    @Lazy
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        BeanConstraintDescriptor beanConstraintDescriptor = new DefaultBeanConstraintDescriptor(beanRegistry(), beanMetadataRepository());
        for (DatabaseConstraintsConfigurer configurer : configurers) {
            configurer.configureConstraintDescriptor(beanConstraintDescriptor);
        }
        return beanConstraintDescriptor;
    }
    
    //
    // Attributes from @EnableDatabaseConstraints
    //

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        attributes = importMetadata.getAnnotationAttributes(EnableDatabaseConstraints.class.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String entityManagerFactoryName = (String) attributes.get(ENTITY_MANAGER_FACTORY_REF);
        String dataSourceName = (String) attributes.get(DATA_SOURCE_REF);
        
        if (applicationContext.containsBean(entityManagerFactoryName)) {
            entityManagerFactory = applicationContext.getBean(entityManagerFactoryName, EntityManagerFactory.class);
            dataSource = HibernateUtils.getDataSource(entityManagerFactory);
        } else {
            dataSource = applicationContext.getBean(dataSourceName, DataSource.class);
        }

        String[] basePackages = (String[]) attributes.get(BASE_PACKAGES_REF);
        Class<?>[] baseClasses = (Class<?>[]) attributes.get(BASE_CLASSES_REF);

        this.basePackages = new HashSet<String>();
        this.basePackages.addAll(Arrays.asList(basePackages));
        for (Class<?> baseClass : baseClasses) {
            String basePackage = getPackage(baseClass);
            this.basePackages.add(basePackage);
        }
    }
    
    private String getPackage(Class<?> baseClass) {
        return StringUtils.substringBeforeLast(baseClass.getName(), ".");
    }

}
