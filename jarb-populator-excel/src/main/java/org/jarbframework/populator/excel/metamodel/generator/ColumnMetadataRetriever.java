package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.springframework.util.Assert;

/**
 * Utility class capable of retrieving required metadata from a column.
 * @author Sander Benschop
 *
 */
public final class ColumnMetadataRetriever {
    /**
     * Gets the Collection's content type. Should only be one in case of ElementCollections.
     * @param property PropertyDefinition to get the content type from.
     * @return Class of content type.
     */
    public static Class<?> getCollectionContentsType(PropertyDefinition property) {
        ParameterizedType type = (ParameterizedType) property.getField().getGenericType();
        Type[] typeArguments = type.getActualTypeArguments();
        Assert.isTrue(typeArguments.length == 1, "ElementCollection collection can only be of one type");
        return (Class<?>) typeArguments[0];
    }
	
	/**
     * Retrieves all the column names of all fields in the passed Class that possess @Column annotations.
     * @param entityClass Class to pull the columnNames from.
     * @return Set of column names.
     */
    public static Set<String> getColumnNamesForClass(Class<?> entityClass) {
        Set<String> columnNames = new HashSet<String>();
        for (Field field : entityClass.getDeclaredFields()) {
            String columnName = getColumnNameFromField(field);
            if (columnName != null) {
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }

    /**
     * Retrieves a column name from an @Column annotation of a field.
     * @param field Field to retrieve column name from
     * @return Column name
     */
    private static String getColumnNameFromField(Field field) {
        String columnName = null;
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !"".equals(columnAnnotation.name())) {
            columnName = columnAnnotation.name();
        } else {
        	columnName = field.getName();
        }
        return columnName;
    }
}
