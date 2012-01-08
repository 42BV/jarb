package org.jarbframework.constraint.database;

import javax.sql.DataSource;

import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.jarbframework.utils.spring.SingletonFactoryBean;

public class JdbcCachingDatabaseConstraintRepositoryFactoryBean extends SingletonFactoryBean<DatabaseConstraintRepository> {

    private SchemaMapper schemaMapper = new JpaHibernateSchemaMapper();
    private DataSource dataSource;
    private String catalog;
    private String schema;
    
    @Override
    protected DatabaseConstraintRepository createObject() throws Exception {
        DatabaseConstraintRepository databaseConstraintRepository = new DatabaseConstraintRepository();
        databaseConstraintRepository.setSchemaMapper(schemaMapper);
        databaseConstraintRepository.setColumnMetadataRepository(buildCachingJdbcColumnRepository());
        return databaseConstraintRepository;
    }
    
    private ColumnMetadataRepository buildCachingJdbcColumnRepository() {
        JdbcColumnMetadataRepository jdbcColumnMetadataRepository = new JdbcColumnMetadataRepository(dataSource);
        jdbcColumnMetadataRepository.setCatalog(catalog);
        jdbcColumnMetadataRepository.setSchema(schema);
        return new CachingColumnMetadataRepository(jdbcColumnMetadataRepository);
    }

    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
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
