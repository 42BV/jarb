package org.jarbframework.utils.orm;

import org.jarbframework.utils.bean.PropertyReference;

/**
 * Maps bean (properties) to database tables and columns.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public interface SchemaMapper {

    /**
     * Retrieve the table name of an entity class.
     * @param clazz type of entity
     * @return name of the table
     * @throws NotAnEntityException whenever the specified bean class could not be recognized as entity
     */
    String tableNameOf(Class<?> clazz);

    /**
     * Retrieve the column that a property maps to, if any.
     * @param property reference to the property
     * @return column reference, if any
     * @throws NotAnEntityException whenever the specified bean class could not be recognized as entity
     */
    ColumnReference columnOf(PropertyReference property);
    
    /**
     * Determine if a class could be recognized as entity.
     * @param clazz type of bean that should be checked
     * @return {@code true} whenever the class is recognized as entity, else {@code false}
     */
    boolean isEntity(Class<?> clazz);
    
    /**
     * Determine if a class could be recognized as embeddable. Embeddable beans can contain properties
     * that actually belong inside the entity's database table.
     * @param clazz type of bean that should be checked
     * @return {@code true} whenever the class is recognized as embeddable, else {@code false}
     */
    boolean isEmbeddable(Class<?> clazz);

}
