package org.jarbframework.populator.excel.metamodel.generator;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.PropertyReference;

/**
 * Creates columnDefinitions for regular fields.
 * @author Sander Benschop
 *
 */
public class RegularColumnGenerator {
    private final FieldAnalyzer fieldAnalyzer;

    public RegularColumnGenerator(EntityManagerFactory entityManagerFactory) {
        this.fieldAnalyzer = new FieldAnalyzer(entityManagerFactory);
    }

    /**
     * Creates a columnDefinition for a regular field.
     * @param propertyReference the property to create a columnDefinition from
     * @return ColumnDefinition
     */
    public PropertyDefinition createColumnDefinitionForRegularField(PropertyReference propertyReference) {
        PropertyDefinition.Builder columnDefinitionBuilder = fieldAnalyzer.analyzeField(propertyReference);
        return columnDefinitionBuilder != null ? columnDefinitionBuilder.build() : null;
    }
}
