/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint;

import nl._42.jarb.constraint.metadata.BeanConstraintDescriptor;
import nl._42.jarb.constraint.metadata.BeanConstraintService;
import nl._42.jarb.constraint.metadata.DefaultBeanConstraintDescriptor;
import nl._42.jarb.constraint.metadata.database.BeanMetadataRepository;
import nl._42.jarb.constraint.metadata.database.BeanMetadataRepositoryFactoryBean;
import nl._42.jarb.constraint.metadata.factory.EntityFactory;
import nl._42.jarb.constraint.metadata.factory.JpaEntityFactory;
import nl._42.jarb.constraint.violation.DatabaseConstraintExceptionTranslator;
import nl._42.jarb.constraint.violation.TranslateAdviceAddingBeanPostProcessor;
import nl._42.jarb.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.ConfigurableViolationResolver;
import nl._42.jarb.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import nl._42.jarb.utils.bean.JpaBeanRegistry;
import nl._42.jarb.utils.orm.hibernate.HibernateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotationMetadata;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Set<DatabaseConstraintsConfigurer> configurers = new HashSet<>();

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
    public DatabaseConstraintExceptionTranslator exceptionTranslator() {
        return new DatabaseConstraintExceptionTranslator(violationResolver(), exceptionFactory());
    }
    
    @Bean
    @Lazy
    public DatabaseConstraintViolationResolver violationResolver() {
        ConfigurableViolationResolver resolver = new ConfigurableViolationResolver(dataSource);
        for (DatabaseConstraintsConfigurer configurer : configurers) {
            configurer.configureViolationResolver(resolver);
        }
        return resolver;
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
    public TranslateAdviceAddingBeanPostProcessor translateAdviceAddingBeanPostProcessor() {
        Class<? extends Annotation> annotation = (Class<? extends Annotation>) attributes.get(PROXY_ANNOTATION_REF);
        return new TranslateAdviceAddingBeanPostProcessor(exceptionTranslator(), annotation);
    }
    
    //
    // Bean metadata descriptions
    //

    @Bean
    @Lazy
    public EntityFactory entityFactory() {
        if (entityManagerFactory != null) {
            return new JpaEntityFactory(entityManagerFactory);
        } else {
            return () -> Collections.emptySet();
        }
    }

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
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        BeanConstraintDescriptor beanConstraintDescriptor = new DefaultBeanConstraintDescriptor(beanMetadataRepository());
        if (entityManagerFactory != null) {
            beanConstraintDescriptor.setBeanRegistry(new JpaBeanRegistry(entityManagerFactory));
        }
        for (DatabaseConstraintsConfigurer configurer : configurers) {
            configurer.configureConstraintDescriptor(beanConstraintDescriptor);
        }
        return beanConstraintDescriptor;
    }

    @Bean
    @Lazy
    public BeanConstraintService beanConstraintService(BeanConstraintDescriptor descriptor, EntityFactory factory) {
        BeanConstraintService constraintService = new BeanConstraintService(descriptor);
        constraintService.registerClasses(factory);
        return constraintService;
    }

    //
    // Attributes from @EnableDatabaseConstraints
    //

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        attributes = importMetadata.getAnnotationAttributes(EnableDatabaseConstraints.class.getName());
    }

    @Override
    public void afterPropertiesSet() {
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

        this.basePackages = new HashSet<>();
        this.basePackages.addAll(Arrays.asList(basePackages));
        for (Class<?> baseClass : baseClasses) {
            this.basePackages.add(baseClass.getPackage().getName());
        }
    }

}
