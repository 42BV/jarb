package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.GeneratedValue;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.BeanProperties;
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

    public PropertyDefinition.Builder analyzeField(PropertyReference propertyReference) {
        PropertyDefinition.Builder columnDefinitionBuilder = null;
        Field field = BeanProperties.findPropertyField(propertyReference);
        if (isCollection(field)) {
            JoinTable joinTable = field.getAnnotation(JoinTable.class);
            if (joinTable != null) {
                columnDefinitionBuilder = joinTableDefinition(joinTable, field);
            }
            // TODO: Implement @ElementCollection
        } else {
            ColumnReference columnRef = schemaMapper.columnOf(propertyReference);
            if (columnRef != null) {
                columnDefinitionBuilder = PropertyDefinition.forField(field).setColumnName(columnRef.getColumnName());
                if (isAssociation(field)) {
                    columnDefinitionBuilder.setDatabaseType(PropertyDatabaseType.REFERENCE);
                }
                if (isGeneratedValue(field)) {
                    columnDefinitionBuilder.valueIsGenerated();
                }
            }
        }
        return columnDefinitionBuilder;
    }

    private boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private boolean isAssociation(Field field) {
        return field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null;
    }

    private boolean isGeneratedValue(Field field) {
        return field.getAnnotation(GeneratedValue.class) != null;
    }

    private PropertyDefinition.Builder joinTableDefinition(JoinTable annotation, Field field) {
        return PropertyDefinition.forField(field).setDatabaseType(PropertyDatabaseType.COLLECTION_REFERENCE).setJoinTableName(annotation.name())
                .setJoinColumnName(annotation.joinColumns()[0].name()).setInverseJoinColumnName(annotation.inverseJoinColumns()[0].name());
    }

}
