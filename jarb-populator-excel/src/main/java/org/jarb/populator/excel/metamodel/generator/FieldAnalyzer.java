package org.jarb.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.apache.commons.lang.StringUtils;
import org.jarb.populator.excel.metamodel.PropertyDatabaseType;
import org.jarb.populator.excel.metamodel.PropertyDefinition;

/**
 * Creates a ColumnDefinition from a field.
 * @author Sander Benschop
 */
public class FieldAnalyzer {
    
    public static PropertyDefinition.Builder analyzeField(final Field field) {
        PropertyDefinition.Builder columnDefinitionBuilder = null;
        final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();
        // Determine column type based on annotation
        for (Annotation annotation : field.getAnnotations()) {
            if(Column.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = columnDefinition((Column) annotation, field);
                break;
            }
            if(JoinColumn.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = joinColumnDefinition((JoinColumn) annotation, field);
                break;
            }
            if(JoinTable.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = joinTableDefinition((JoinTable) annotation, field);
                break;
            }
            annotationClasses.add(annotation.annotationType());
        }
        // Whenever no annotation could be found, and the field is not relational, create a regular column
        if (columnDefinitionBuilder == null && !containsRelationalAnnotation(annotationClasses)) {
            columnDefinitionBuilder = PropertyDefinition.forField(field).setColumnName(field.getName());
        }
        if(columnDefinitionBuilder != null) {
            if (field.getAnnotation(javax.persistence.GeneratedValue.class) != null) {
                columnDefinitionBuilder.valueIsGenerated();
            }
        }
        return columnDefinitionBuilder;
    }
    
    private static PropertyDefinition.Builder columnDefinition(Column annotation, Field field) {
        String columnName = annotation.name();
        if(StringUtils.isBlank(columnName)) {
            columnName = field.getName();
        }
        return PropertyDefinition.forField(field).setColumnName(columnName);
    }
    
    private static PropertyDefinition.Builder joinColumnDefinition(JoinColumn annotation, Field field) {
        return PropertyDefinition.forField(field).setColumnName(annotation.name()).setDatabaseType(PropertyDatabaseType.JOIN_COLUMN);
    }
    
    private static PropertyDefinition.Builder joinTableDefinition(JoinTable annotation, Field field) {
        return PropertyDefinition.forField(field)
            .setDatabaseType(PropertyDatabaseType.JOIN_TABLE)
            .setJoinTableName(annotation.name())
            .setJoinColumnName(annotation.joinColumns()[0].name())
            .setInverseJoinColumnName(annotation.inverseJoinColumns()[0].name());
    }

    private static boolean containsRelationalAnnotation(Set<Class<? extends Annotation>> annotationTypeClasses) {
        return
            annotationTypeClasses.contains(javax.persistence.OneToMany.class) ||
            annotationTypeClasses.contains(javax.persistence.ManyToOne.class) ||
            annotationTypeClasses.contains(javax.persistence.ManyToMany.class);
    }
    
}
