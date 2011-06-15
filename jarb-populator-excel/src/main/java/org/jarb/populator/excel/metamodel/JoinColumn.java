package org.jarb.populator.excel.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Used for joining a one-to-many table, extends from ColumnDefinition.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public class JoinColumn extends ColumnDefinition {

    public JoinColumn(String fieldName) {
        super(fieldName);
    }

    /**
     * Sets ColumnName from many-side to ColumnDefinition.
     * @param annotation Annotation containing the annotationtype, which is in this case JoinTable
     */
    @Override
    public void storeAnnotation(Field field, Annotation annotation) {
        setColumnName(((javax.persistence.JoinColumn) annotation).name());
    }
}
