package org.jarb.constraint.database.column;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.ReflectionUtils;

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
        String tableName = null;
        // First look for a table annotation, containing a custom table name
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            tableName = tableAnnotation.name();
        }
        // Otherwise use the entity name
        if (isBlank(tableName) && isEntity(entityClass)) {
            tableName = getEntityName(entityClass);
        }
        return tableName;
    }

    /**
     * Determine if an entity class is annotated with @Entity.
     * @param entityClass class of the entity
     * @return {@code true} if it is annotated, else {@code false}
     */
    private boolean isEntity(Class<?> entityClass) {
        return entityClass.getAnnotation(Entity.class) != null;
    }

    /**
     * Retrieve the name of our entity. This method should only be used for
     * classes that are directly annotated with @Entity.
     * @param entityClass class of the entity
     * @return name of the entity, based on the annotation or simple class name
     */
    private String getEntityName(Class<?> entityClass) {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        // Retrieve name directly from our annotation
        String entityName = entityAnnotation.name();
        if (isBlank(entityName)) {
            // Or use the simple class name if the name attribute is blank
            entityName = entityClass.getSimpleName();
        }
        return entityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(Class<?> containerClass, String fieldName) {
        Field field = ReflectionUtils.findField(containerClass, fieldName);
        if (field == null) {
            return null;
        }
        String columnName = null;
        // Attempt to retrieve the column name from annotation
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            columnName = columnAnnotation.name();
        }
        // Otherwise use the field name
        if (isBlank(columnName)) {
            columnName = fieldName;
        }
        return columnName;
    }
}
