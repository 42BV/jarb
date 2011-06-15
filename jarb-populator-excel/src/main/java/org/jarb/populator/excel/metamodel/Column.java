package org.jarb.populator.excel.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

/**
 * Stores annotations or values of a column, extends from ColumnDefinition.
 * @author Willem Eppen
 * @author Sander Benschop
 * 
 */
public class Column extends ColumnDefinition {

    public Column(String fieldName) {
        super(fieldName);
    }

    /**
     * Sets the annotationtype of a column, overrides the function in ColumnDefinition.
     * @param annotation Annotation to be set
     */
    @Override
    public void storeAnnotation(Field field, Annotation annotation) {
        String columnName = ((javax.persistence.Column) annotation).name();
        if (StringUtils.isBlank(columnName)) {
            columnName = field.getName();
        }
        setColumnName(columnName);
    }
}
