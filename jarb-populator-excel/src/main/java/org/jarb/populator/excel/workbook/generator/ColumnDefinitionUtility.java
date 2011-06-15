package org.jarb.populator.excel.workbook.generator;

import java.util.HashSet;
import java.util.Set;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnDefinition;

/**
 * Can gather all the associative Columndefinitions (JoinTable's) from a ClassDefinition.
 * Can also determine a proper field name for a columnDefinition
 * @author Sander Benschop
 *
 */
public final class ColumnDefinitionUtility {

    /** Private constructor. */
    private ColumnDefinitionUtility() {
    }

    /**
     * Gathers all JoinTable columnDefinitions from the ClassDefinition.
     * @param classDefinition
     * @return Set of ColumnDefinitions that are associative tables (JoinTable)
     */
    protected static Set<ColumnDefinition> gatherAssociativeColumnDefinitions(ClassDefinition<?> classDefinition) {
        Set<ColumnDefinition> associativeColumnDefinitions = new HashSet<ColumnDefinition>();
        //Check for @JoinTables. Iterating over all columnDefinitions was also considered but this makes searching for cells problematic
        //if the cells are @JoinTables (which means they're not in this sheet.
        for (ColumnDefinition columnDefinition : classDefinition.getPropertyDefinitions()) {
            if (columnDefinition.isAssociativeTable()) {
                associativeColumnDefinitions.add(columnDefinition);
            }
        }
        return associativeColumnDefinitions;
    }

    /**
     * Determines the ColumnDefinition's fieldname.
     * @param classDefinition Instance of ClassDefinition which is a representation of a database table
     * @param columnName Column name to search the fieldname with.
     * @return Fieldname
     */
    protected static String getFieldName(ClassDefinition<?> classDefinition, String columnName) {
        String fieldName;
        if ("id".equals(columnName)) {
            fieldName = "id";
        } else {
            fieldName = classDefinition.getPropertyDefinitionByColumnName(columnName).getFieldName();
        }
        return fieldName;
    }

}
