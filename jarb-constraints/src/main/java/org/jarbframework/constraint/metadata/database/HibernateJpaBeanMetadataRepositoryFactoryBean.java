/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.database;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.hibernate.HibernateJpaSchemaMapper;
import org.jarbframework.utils.orm.hibernate.HibernateUtils;
import org.jarbframework.utils.spring.SingletonFactoryBean;

public class HibernateJpaBeanMetadataRepositoryFactoryBean extends SingletonFactoryBean<BeanMetadataRepository> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public HibernateJpaBeanMetadataRepositoryFactoryBean(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    protected BeanMetadataRepository createObject() throws Exception {
        DataSource dataSource = HibernateUtils.getDataSource(entityManagerFactory);
        ColumnMetadataRepository columnMetadataRepository = new JdbcColumnMetadataRepository(dataSource);
        SchemaMapper schemaMapper = new HibernateJpaSchemaMapper(entityManagerFactory);
        
        return new CachingBeanMetadataRepository(new SimpleBeanMetadataRepository(columnMetadataRepository, schemaMapper));
    }
    
}
