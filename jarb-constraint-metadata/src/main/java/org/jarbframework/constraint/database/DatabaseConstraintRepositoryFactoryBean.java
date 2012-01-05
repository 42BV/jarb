/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.database;

import javax.sql.DataSource;

import org.jarbframework.constraint.database.column.CachingColumnMetadataRepository;
import org.jarbframework.constraint.database.column.ColumnMetadataProvider;
import org.jarbframework.constraint.database.column.JdbcColumnMetadataProvider;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Capable of building a caching database constraint repository.
 *
 * @author Jeroen van Schagen
 * @date Sep 6, 2011
 */
public class DatabaseConstraintRepositoryFactoryBean extends SingletonFactoryBean<DatabaseConstraintRepository> {

    private SchemaMapper schemaMapper = new JpaHibernateSchemaMapper();
    private ColumnMetadataProvider columnMetadataProvider;

    public void setDataSource(DataSource dataSource) {
        columnMetadataProvider = new JdbcColumnMetadataProvider(dataSource);
    }

    public void setColumnMetadataProvider(ColumnMetadataProvider columnMetadataProvider) {
        this.columnMetadataProvider = columnMetadataProvider;
    }

    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    @Override
    protected DatabaseConstraintRepository createObject() throws Exception {
        DatabaseConstraintRepositoryImpl databaseConstraintRepository = new DatabaseConstraintRepositoryImpl();
        databaseConstraintRepository.setColumnMetadataRepository(new CachingColumnMetadataRepository(columnMetadataProvider));
        databaseConstraintRepository.setSchemaMapper(schemaMapper);
        return databaseConstraintRepository;
    }

}
