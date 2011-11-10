/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.populator.excel.workbook.validator;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ColumnMetadataRetriever;

class WorkbookExpectation {
    private Set<String> sheetNames = new HashSet<String>();
    private Map<String, Set<String>> columnNamesMap = new HashMap<String, Set<String>>();

    public WorkbookExpectation(MetaModel metamodel) {
        for (Definition<?> entity : metamodel.entities()) {
            final String sheetName = entity.getTableName();
            // Each entity type has a specific sheet name
            sheetNames.add(sheetName);
            Set<String> columnNames = new HashSet<String>();

            addDiscriminatorColumnNameIfApplicable(entity, columnNames);

            for (PropertyDefinition property : entity.properties()) {
                if (property.getDatabaseType() == PropertyDatabaseType.COLLECTION_REFERENCE) {
                    // Join table properties got their own sheet
                    final String joinSheetName = property.getJoinTableName();
                    sheetNames.add(joinSheetName);
                    // With the join and inverse join as columns
                    Set<String> joinColumnNames = new HashSet<String>();
                    joinColumnNames.add(property.getJoinColumnName());
                    joinColumnNames.add(property.getInverseJoinColumnName());
                    columnNamesMap.put(joinSheetName, joinColumnNames);
                } else if (property.getDatabaseType() == PropertyDatabaseType.ELEMENT_COLLECTION) {
                    //Element collections have their own sheet
                    final String elementCollectionTableName = property.getName();
                    sheetNames.add(elementCollectionTableName);

                    //With the joinColumns and its own columns as columns
                    Set<String> elementCollectionColumnNames = new HashSet<String>();
                    elementCollectionColumnNames.addAll(property.getElementCollectionJoinColumnNames());
                    elementCollectionColumnNames.addAll(ColumnMetadataRetriever.getColumnNamesForClass(ColumnMetadataRetriever
                            .getCollectionContentsType(property)));
                    columnNamesMap.put(elementCollectionTableName, elementCollectionColumnNames);
                } else {
                    // Regular properties are mapped to a column name
                    columnNames.add(property.getColumnName());
                }
            }
            columnNamesMap.put(sheetName, columnNames);
        }
    }

    /**
     * Adds a discriminator column name if the Definition is of the EntityDefinition type and has a discriminator column.
     * @param entity Entity to get the discriminator column name from.
     * @param columnNames Collection of column names.
     */
    private void addDiscriminatorColumnNameIfApplicable(Definition<?> entity,
            Set<String> columnNames) {
        if (entity instanceof EntityDefinition<?>) {
            EntityDefinition<?> entityDefinition = (EntityDefinition<?>) entity;
            if (entityDefinition.hasDiscriminatorColumn()) {
                columnNames.add(entityDefinition.getDiscriminatorColumnName());
            }
        }
    }

    public Set<String> getSheetNames() {
        return unmodifiableSet(sheetNames);
    }

    public boolean isExpectedSheet(String sheetName) {
        return sheetNames.contains(sheetName);
    }

    public Set<String> getColumnNames(String sheetName) {
        Set<String> columnNames = columnNamesMap.get(sheetName);
        if (columnNames == null) {
            return emptySet();
        } else {
            return unmodifiableSet(columnNames);
        }
    }

    public boolean isExpectedColumn(String sheetName, String columnName) {
        return getColumnNames(sheetName).contains(columnName);
    }
}