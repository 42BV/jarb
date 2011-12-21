package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyPath;
import org.jarbframework.utils.bean.BeanProperties;
import org.jarbframework.utils.bean.PropertyReference;
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
     * @param propertyReference embedded property to create ColumnDefinitions from 
     */
    public List<PropertyDefinition> createColumnDefinitionsForEmbeddedField(PropertyReference propertyReference) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        // This means there are embedded attributes available. Find all attributes in the embeddable class.
        Field embeddableField = BeanProperties.findPropertyField(propertyReference);
        for (Field embeddedPropertyField : embeddableField.getType().getDeclaredFields()) {
            if (!ReflectionUtils.isPublicStaticFinal(embeddedPropertyField)) {
                PropertyReference embeddablePropertyReference = new PropertyReference(propertyReference, embeddedPropertyField.getName());
                PropertyDefinition.Builder columnDefinitionBuilder = new FieldAnalyzer(schemaMapper).analyzeField(embeddablePropertyReference);
                if (columnDefinitionBuilder != null) {
                    columnDefinitionBuilder.setEmbeddablePath(PropertyPath.startingFrom(embeddableField));
                    columnDefinitions.add(columnDefinitionBuilder.build());
                }
            }
        }
        return columnDefinitions;
    }

}
