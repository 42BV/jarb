package org.jarb.utils.database;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

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
     * @param entityClass class of the entity containing our property
     * @param propertyName name of the property
     * @return column name of the property
     */
    public static String getColumnName(Class<?> entityClass, String propertyName) {
        Field field = ReflectionUtils.findField(entityClass, propertyName);
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
    
//    /**
//     * Retrieve all java types represented in a collection of entity types.
//     * @param entityTypes each entity type that should be included
//     * @return java types for each provided entity type
//     */
//    public static Collection<Class<?>> getJavaTypes(Collection<EntityType<?>> entityTypes) {
//        Set<Class<?>> javaTypes = new LinkedHashSet<Class<?>>();
//        for(EntityType<?> entityType : entityTypes) {
//            javaTypes.add(entityType.getJavaType());
//        }
//        return javaTypes;
//    }
    
    /**
     * Retrieve all "root" entities described in our JPA metamodel.
     * @param metamodel JPA metamodel, containing all entity descriptions
     * @return entity types for each root entity class
     */
    public static Collection<EntityType<?>> getRootEntities(Metamodel metamodel) {
        Set<EntityType<?>> entityTypes = new HashSet<EntityType<?>>();
        for(EntityType<?> entityType : metamodel.getEntities()) {            
            if(!hasEntitySuperClass(entityType.getJavaType())) {
                entityTypes.add(entityType);
            }
        }
        return entityTypes;
    }
    
    /**
     * Determine if an entity has a one or more entity super classes.
     * @param entityClass class of the entity to check
     * @return {@code true} if one or more entity super classes were found, else {@code false}
     */
    private static boolean hasEntitySuperClass(Class<?> entityClass) {
        boolean found = false;
        Class<?> currentClass = entityClass;
        while(currentClass.getSuperclass() != null) {
            final Class<?> superClass = currentClass.getSuperclass();
            if(isEntity(superClass)) {
                found = true;
                break;
            }
            currentClass = superClass;
        }
        return found;
    }
    
}
