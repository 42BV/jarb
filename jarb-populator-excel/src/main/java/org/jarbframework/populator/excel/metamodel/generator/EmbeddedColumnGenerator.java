package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyPath;
import org.jarbframework.utils.orm.SchemaMapper;
import org.springframework.util.ReflectionUtils;

/**
 * Creates columnDefinitions for embedded fields.
 * @author Sander Benschop
 *
 */
public final class EmbeddedColumnGenerator {
    private final SchemaMapper schemaMapper;

    public EmbeddedColumnGenerator(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    /**
     * Returns a list of ColumnDefinitions for an embedded field.
     * @param field Embedded field to create ColumnDefinitions from 
     * @return List of ColumnDefinitions
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public List<PropertyDefinition> createColumnDefinitionsForEmbeddedField(Field embeddableField, Class<?> entityClass) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        // This means there are embedded attributes available. Find all attributes in the embeddable class.
        for (Field embeddedPropertyField : embeddableField.getType().getDeclaredFields()) {
            if (!ReflectionUtils.isPublicStaticFinal(embeddedPropertyField)) {
                PropertyDefinition.Builder columnDefinitionBuilder = new FieldAnalyzer(schemaMapper).analyzeField(embeddedPropertyField, entityClass);
                if (columnDefinitionBuilder != null) {
                    columnDefinitionBuilder.setEmbeddablePath(PropertyPath.startingFrom(embeddableField));
                    overrideAttributes(embeddableField, columnDefinitionBuilder, embeddedPropertyField);
                    columnDefinitions.add(columnDefinitionBuilder.build());
                }
            }
        }
        return columnDefinitions;
    }

    private void overrideAttributes(Field field, PropertyDefinition.Builder columnDefinitionBuilder, Field embeddedField) {
        javax.persistence.AttributeOverrides annotation = field.getAnnotation(javax.persistence.AttributeOverrides.class);
        if (annotation != null) {
            for (AttributeOverride overrideAnnotation : annotation.value()) {
                if (overrideAnnotation.name().equals(embeddedField.getName())) {
                    columnDefinitionBuilder.setColumnName(overrideAnnotation.column().name());
                }
            }
        }
    }

}
