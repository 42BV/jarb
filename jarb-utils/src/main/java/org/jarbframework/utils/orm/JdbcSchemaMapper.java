/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm;

import org.jarbframework.utils.bean.PropertyReference;

/**
 * JDBC implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class JdbcSchemaMapper implements SchemaMapper {
    
    @Override
    public String getTableName(Class<?> beanClass) {
        return unqualify(beanClass.getSimpleName());
    }

    @Override
    public ColumnReference getColumnReference(PropertyReference propertyReference) {
        String tableName = getTableName(propertyReference.getBeanClass());
        String columnName = unqualify(propertyReference.getName());
        return new ColumnReference(tableName, columnName);
    }
    
    private String unqualify(String value) {
        return value.toLowerCase();
    }

    @Override
    public boolean isEmbeddable(Class<?> clazz) {
        return false;
    }
    
}
