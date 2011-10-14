package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * Creates a ColumnDefinition from a field.
 * @author Sander Benschop
 */
public class FieldAnalyzer {
    private final SchemaMapper schemaMapper;

    public FieldAnalyzer(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    public PropertyDefinition.Builder analyzeField(Field field, Class<?> entityClass) {
        PropertyDefinition.Builder columnDefinitionBuilder = null;
        final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();
        // Determine column type based on annotation
        for (Annotation annotation : field.getAnnotations()) {
            if (Column.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = columnDefinition((Column) annotation, field);
                break;
            }
            if (JoinColumn.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = joinColumnDefinition((JoinColumn) annotation, field);
                break;
            }
            if (JoinTable.class.isAssignableFrom(annotation.getClass())) {
                columnDefinitionBuilder = joinTableDefinition((JoinTable) annotation, field);
                break;
            }
            annotationClasses.add(annotation.annotationType());
        }
        // Whenever no annotation could be found, and the field is not relational, create a regular column
        if (columnDefinitionBuilder == null && !isCollection(field)) {
            ColumnReference columnRef = schemaMapper.columnOf(new PropertyReference(entityClass, field.getName()));
            if (columnRef != null) {
                columnDefinitionBuilder = PropertyDefinition.forField(field).setColumnName(columnRef.getColumnName());
            }
        }
        if (columnDefinitionBuilder != null && field.getAnnotation(javax.persistence.GeneratedValue.class) != null) {
            columnDefinitionBuilder.valueIsGenerated();
        }
        return columnDefinitionBuilder;
    }

    private boolean isCollection(Field field) {
        return Collections.class.isAssignableFrom(field.getType());
    }

    private PropertyDefinition.Builder columnDefinition(Column annotation, Field field) {
        String columnName = annotation.name();
        if (StringUtils.isBlank(columnName)) {
            columnName = field.getName();
        }
        return PropertyDefinition.forField(field).setColumnName(columnName);
    }

    private PropertyDefinition.Builder joinColumnDefinition(JoinColumn annotation, Field field) {
        return PropertyDefinition.forField(field).setColumnName(annotation.name()).setDatabaseType(PropertyDatabaseType.JOIN_COLUMN);
    }

    private PropertyDefinition.Builder joinTableDefinition(JoinTable annotation, Field field) {
        return PropertyDefinition.forField(field).setDatabaseType(PropertyDatabaseType.JOIN_TABLE).setJoinTableName(annotation.name())
                .setJoinColumnName(annotation.joinColumns()[0].name()).setInverseJoinColumnName(annotation.inverseJoinColumns()[0].name());
    }

}
