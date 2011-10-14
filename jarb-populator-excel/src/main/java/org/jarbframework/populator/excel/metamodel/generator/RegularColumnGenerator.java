package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * Creates columnDefinitions for regular fields.
 * @author Sander Benschop
 *
 */
public class RegularColumnGenerator {
    private final FieldAnalyzer fieldAnalyzer;

    public RegularColumnGenerator(SchemaMapper schemaMapper) {
        this.fieldAnalyzer = new FieldAnalyzer(schemaMapper);
    }

    /**
     * Creates a columnDefinition for a regular field.
     * @param field To create the columnDefinition from
     * @param entityClass 
     * @return ColumnDefinition
     */
    public PropertyDefinition createColumnDefinitionForRegularField(Field field, Class<?> entityClass) {
        PropertyDefinition.Builder columnDefinitionBuilder = fieldAnalyzer.analyzeField(field, entityClass);
        return columnDefinitionBuilder != null ? columnDefinitionBuilder.build() : null;
    }
}
