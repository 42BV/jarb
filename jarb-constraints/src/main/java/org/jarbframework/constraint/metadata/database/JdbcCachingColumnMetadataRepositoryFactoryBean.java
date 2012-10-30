package org.jarbframework.constraint.metadata.database;

import javax.sql.DataSource;

import org.jarbframework.utils.spring.SingletonFactoryBean;

public class JdbcCachingColumnMetadataRepositoryFactoryBean extends SingletonFactoryBean<ColumnMetadataRepository> {

    private DataSource dataSource;
    private String catalog;
    private String schema;

    @Override
    protected ColumnMetadataRepository createObject() throws Exception {
        JdbcColumnMetadataRepository jdbcColumnMetadataRepository = new JdbcColumnMetadataRepository(dataSource);
        jdbcColumnMetadataRepository.setCatalog(catalog);
        jdbcColumnMetadataRepository.setSchema(schema);
        return new CachingColumnMetadataRepository(jdbcColumnMetadataRepository);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
