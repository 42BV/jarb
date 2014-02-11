/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.ejb.HibernatePersistence;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.constraint.metadata.database.HibernateJpaColumnMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.TranslateExceptionsBeanDefinitionParser.ExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.TranslateExceptionsBeanPostProcessor;
import org.jarbframework.migrations.MigratingDataSource;
import org.jarbframework.migrations.liquibase.LiquibaseMigrator;
import org.jarbframework.populator.DatabasePopulatingApplicationListener;
import org.jarbframework.populator.DatabasePopulatorChain;
import org.jarbframework.populator.ProductSpecificSqlClassPathResourceDatabasePopulator;
import org.jarbframework.populator.SqlResourceDatabasePopulator;
import org.jarbframework.populator.excel.ExcelDatabasePopulator;
import org.jarbframework.sample.PostPopulator;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.hibernate.ConventionNamingStrategy;
import org.jarbframework.utils.orm.hibernate.HibernateJpaSchemaMapper;
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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.jarbframework.sample", 
               excludeFilters = { @Filter(Controller.class), @Filter(Configuration.class) })
@EnableJpaRepositories(basePackages = "org.jarbframework.sample")
public class ApplicationConfig {
    
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabase embeddedDataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
        return createMigratingDataSource(embeddedDataSource);
    }
    
    private static DataSource createMigratingDataSource(DataSource dataSource) {
        MigratingDataSource migratingDataSource = new MigratingDataSource();
        migratingDataSource.setDelegate(dataSource);
        migratingDataSource.setMigrator(new LiquibaseMigrator("src/main/db"));
        return migratingDataSource;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);
        entityManagerFactoryBean.setPackagesToScan("org.jarbframework.sample");
        
        Map<String, Object> jpaProperties = new HashMap<>();
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
    
    // TODO: Exception translation
    
    @Bean
    public DatabaseConstraintExceptionTranslator exceptionTranslator() throws Exception {
        ExceptionTranslatorFactoryBean translatorFactoryBean = new ExceptionTranslatorFactoryBean();
        translatorFactoryBean.setBasePackage("org.jarbframework.sample");
        translatorFactoryBean.setDataSource(dataSource());
        return translatorFactoryBean.getObject();
    }
    
    @Bean
    public TranslateExceptionsBeanPostProcessor translateExceptionBeanPostProcessor() throws Exception {
        TranslateExceptionsBeanPostProcessor translateExceptionsBeanPostProcessor = new TranslateExceptionsBeanPostProcessor();
        translateExceptionsBeanPostProcessor.setTranslator(exceptionTranslator());
        return translateExceptionsBeanPostProcessor;
    }
    
    // TODO: Enable metadata

    @Bean
    public SchemaMapper schemaMapper() {
        return new HibernateJpaSchemaMapper(entityManagerFactory().getObject());
    }
    
    @Bean
    public ColumnMetadataRepository columnMetadataRepository() throws Exception {
        HibernateJpaColumnMetadataRepositoryFactoryBean columnMetadataRepositoryFactoryBean = new HibernateJpaColumnMetadataRepositoryFactoryBean();
        columnMetadataRepositoryFactoryBean.setEntityManagerFactory(entityManagerFactory().getObject());
        return columnMetadataRepositoryFactoryBean.getObject();
    }
    
    @Bean
    public BeanConstraintDescriptor beanConstraintDescriptor() throws Exception {
        BeanConstraintDescriptorFactoryBean beanConstraintDescriptorFactoryBean = new BeanConstraintDescriptorFactoryBean();
        beanConstraintDescriptorFactoryBean.setColumnMetadataRepository(columnMetadataRepository());
        beanConstraintDescriptorFactoryBean.setSchemaMapper(schemaMapper());
        return beanConstraintDescriptorFactoryBean.getObject();
    }
    
    @Profile("demo")
    @Configuration
    public static class DemoConfig {
        
        @Autowired
        private DataSource dataSource;
        
        @Autowired
        private EntityManagerFactory entityManagerFactory;
        
        @Bean
        public DatabasePopulatingApplicationListener databasePopulatingApplicationListener() {
            DatabasePopulatingApplicationListener listener = new DatabasePopulatingApplicationListener();
            
            DatabasePopulatorChain initializerChain = new DatabasePopulatorChain();
            initializerChain.add(new ProductSpecificSqlClassPathResourceDatabasePopulator(dataSource, "import.sql"));
            initializerChain.add(new ExcelDatabasePopulator(entityManagerFactory, new ClassPathResource("import.xls")));
            initializerChain.add(postPopulator());
            listener.setInitializer(initializerChain);
            
            listener.setDestroyer(new SqlResourceDatabasePopulator(dataSource, new ClassPathResource("clean.sql")));
            
            return listener;
        }
        
        @Bean
        public PostPopulator postPopulator() {
            return new PostPopulator();
        }

    }

}