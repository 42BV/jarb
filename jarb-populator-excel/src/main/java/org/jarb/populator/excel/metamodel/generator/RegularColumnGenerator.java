package org.jarb.populator.excel.metamodel.generator;

import java.lang.reflect.Field;

import org.jarb.populator.excel.metamodel.PropertyDefinition;

/**
 * Creates columnDefinitions for regular fields.
 * @author Sander Benschop
 *
 */
public final class RegularColumnGenerator {

    /** Private constructor. */
    private RegularColumnGenerator() {
    }

    /**
     * Creates a columnDefinition for a regular field.
     * @param field To create the columnDefinition from
     * @return ColumnDefinition
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public static PropertyDefinition createColumnDefinitionForRegularField(Field field) throws InstantiationException, IllegalAccessException {
        PropertyDefinition.Builder columnDefinitionBuilder = FieldAnalyzer.analyzeField(field);
        return columnDefinitionBuilder != null ? columnDefinitionBuilder.build() : null;
    }

}
