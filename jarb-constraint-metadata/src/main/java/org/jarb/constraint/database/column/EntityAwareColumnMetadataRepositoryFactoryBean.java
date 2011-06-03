package org.jarb.constraint.database.column;

import javax.sql.DataSource;

import org.jarb.utils.spring.SingletonFactoryBean;
import org.springframework.util.Assert;

/**
 * Provides an entity-aware column metadata repository. This factory bean
 * requires a valid data source property.
 * 
 * @author Jeroen van Schagen
 * @since 03-06-2011
 */
public class EntityAwareColumnMetadataRepositoryFactoryBean extends SingletonFactoryBean<EntityAwareColumnMetadataRepository> {
    private DataSource dataSource;
    private TableMapper tableMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    protected EntityAwareColumnMetadataRepository createObject() throws Exception {
        Assert.notNull(dataSource, "Property 'data source' cannot be empty.");
        ColumnMetadataRepository metadataRepository = new CachingColumnMetadataRepository(new JdbcColumnMetadataProvider(dataSource));
        EntityAwareColumnMetadataRepository instance = new EntityAwareColumnMetadataRepository(metadataRepository);
        if (tableMapper != null) { // Only overwrite default if a not null value has been provided
            instance.setTableMapper(tableMapper);
        }
        return instance;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTableMapper(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

}
