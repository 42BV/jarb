/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.orm;

import nl._42.jarb.utils.bean.PropertyReference;

import nl._42.jarb.utils.StringUtils;
import nl._42.jarb.utils.bean.PropertyReference;

/**
 * JDBC implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class JdbcSchemaMapper implements SchemaMapper {

    @Override
    public ColumnReference getColumnReference(PropertyReference property) {
        String tableName = getTableName(property.getBeanClass());
        String columnName = StringUtils.lowerCaseWithUnderscores(property.getPropertyName());
        return new ColumnReference(tableName, columnName);
    }

    private String getTableName(Class<?> beanClass) {
        return StringUtils.lowerCaseWithUnderscores(beanClass.getSimpleName());
    }

    @Override
    public boolean isEmbeddable(Class<?> clazz) {
        return false;
    }
    
}
