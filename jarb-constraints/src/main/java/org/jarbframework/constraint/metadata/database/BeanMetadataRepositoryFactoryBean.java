/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.database;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.utils.SingletonFactoryBean;
import org.jarbframework.utils.orm.JdbcSchemaMapper;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.hibernate.HibernateJpaSchemaMapper;
import org.jarbframework.utils.orm.hibernate.HibernateUtils;

public class BeanMetadataRepositoryFactoryBean extends SingletonFactoryBean<BeanMetadataRepository> {
    
    private final ColumnMetadataRepository columnMetadataRepository;
    
    private final SchemaMapper schemaMapper;
    
    public BeanMetadataRepositoryFactoryBean(EntityManagerFactory entityManagerFactory) {
        this(new JdbcColumnMetadataRepository(HibernateUtils.getDataSource(entityManagerFactory)),
             new HibernateJpaSchemaMapper(entityManagerFactory));
    }
    
    public BeanMetadataRepositoryFactoryBean(DataSource dataSource) {
        this(new JdbcColumnMetadataRepository(dataSource), new JdbcSchemaMapper());
    }

    public BeanMetadataRepositoryFactoryBean(ColumnMetadataRepository columnMetadataRepository,
                                                         SchemaMapper schemaMapper) {
        this.columnMetadataRepository = columnMetadataRepository;
        this.schemaMapper = schemaMapper;
    }
    
    @Override
    protected BeanMetadataRepository createObject() throws Exception {
        BeanMetadataRepository beanMetadataRepository = new SimpleBeanMetadataRepository(columnMetadataRepository, schemaMapper);
        return new CachingBeanMetadataRepository(beanMetadataRepository);
    }
    
}
