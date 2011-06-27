package org.jarb.constraint.database.column;

import org.jarb.utils.database.JpaMetaModelUtils;

/**
 * Table mapper that works using Java Persistence API (JPA) annotations.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class JpaTableMapper implements TableMapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName(Class<?> entityClass) {
        return JpaMetaModelUtils.getTableName(entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(Class<?> containerClass, String propertyName) {
        return JpaMetaModelUtils.getColumnName(containerClass, propertyName);
    }
    
}
