package org.jarb.utils.database;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.ReflectionUtils;

/**
 * Table mapper that works using Java Persistence API (JPA) annotations.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class JpaMetaModelUtils {
    
    /**
     * Determine if a bean class is annotated with @Entity.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEntity(Class<?> beanClass) {
        return beanClass.getAnnotation(Entity.class) != null;
    }
    
    /**
     * Determine if a bean class is annotated with @Embeddable.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEmbeddable(Class<?> beanClass) {
        return beanClass.getAnnotation(Embeddable.class) != null;
    }

    /**
     * Retrieve the table name of an entity.
     * @param entityClass class of the entity
     * @return table name of the entity
     */
    public static String getTableName(Class<?> entityClass) {
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
     * Retrieve the name of our entity.
     * @param entityClass class of the entity, has to be an @Entity
     * @return name of the entity, based on the annotation or simple class name
     */
    private static String getEntityName(Class<?> entityClass) {
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
     * Retrieve the column name of a property.
     * @param containerClass class of the bean containing our property
     * @param propertyName name of the property
     * @return column name of the property
     */
    public static String getColumnName(Class<?> containerClass, String propertyName) {
        Field field = ReflectionUtils.findField(containerClass, propertyName);
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
            columnName = propertyName;
        }
        return columnName;
    }
}
