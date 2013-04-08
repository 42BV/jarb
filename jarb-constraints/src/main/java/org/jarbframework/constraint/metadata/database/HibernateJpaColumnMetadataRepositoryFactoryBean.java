package org.jarbframework.constraint.metadata.database;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.utils.orm.hibernate.HibernateUtils;
import org.jarbframework.utils.spring.SingletonFactoryBean;

public class HibernateJpaColumnMetadataRepositoryFactoryBean extends SingletonFactoryBean<ColumnMetadataRepository> {

    private EntityManagerFactory entityManagerFactory;
    
    private String catalog;
    
    private String schema;
    
    private boolean caching;

    @Override
    protected ColumnMetadataRepository createObject() throws Exception {
        ColumnMetadataRepository columnMetadataRepository = createJdbcColumnMetadataRepository();
        if (caching) {
            columnMetadataRepository = new CachingColumnMetadataRepository(columnMetadataRepository);
        }
        return columnMetadataRepository;
    }

    private JdbcColumnMetadataRepository createJdbcColumnMetadataRepository() {
        DataSource dataSource = HibernateUtils.getDataSource(entityManagerFactory);
        
        JdbcColumnMetadataRepository jdbcColumnMetadataRepository = new JdbcColumnMetadataRepository(dataSource);
        jdbcColumnMetadataRepository.setCatalog(catalog);
        jdbcColumnMetadataRepository.setSchema(schema);
        return jdbcColumnMetadataRepository;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public void setCaching(boolean caching) {
        this.caching = caching;
    }

}
