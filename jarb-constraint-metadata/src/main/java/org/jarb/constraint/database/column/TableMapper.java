package org.jarb.constraint.database.column;

/**
 * Maps entities, and their properties, on to table- and column names.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public interface TableMapper {

    /**
     * Retrieve the table name of a specific entity.
     * @param entityClass entity class for which to retrieve the table name
     * @return table name of the entity, or {@code null} if no table could be resolved
     */
    String getTableName(Class<?> entityClass);

    /**
     * Retrieve the column name of a specific property.
     * @param containerClass class that contains the property
     * @param fieldName field for which we want to retrieve the column name
     * @return column name of the property, or {@code null} if no column could be resolved
     */
    String getColumnName(Class<?> containerClass, String fieldName);

}
