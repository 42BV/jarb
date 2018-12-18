package nl._42.jarb.utils.orm;

import nl._42.jarb.utils.bean.PropertyReference;

import nl._42.jarb.utils.bean.PropertyReference;

/**
 * Maps bean (properties) to database tables and columns.
 *
 * @author Jeroen van Schagen
 * @since Aug 16, 2011
 */
public interface SchemaMapper {

    /**
     * Retrieve the table name of an entity class.
     * @param beanClass type of entity
     * @return name of the table
     */
    String getTableName(Class<?> beanClass);

    /**
     * Retrieve the column that a property maps to, if any.
     * @param propertyReference reference to the property
     * @return column reference, if any
     */
    ColumnReference getColumnReference(PropertyReference propertyReference);

    /**
     * Determine if a class could be recognized as embeddable. Embeddable beans can contain properties
     * that actually belong inside the entity's database table.
     * @param clazz type of bean that should be checked
     * @return {@code true} whenever the class is recognized as embeddable, else {@code false}
     */
    boolean isEmbeddable(Class<?> clazz);

}
