package org.jarbframework.populator.excel.mapping.importer;

import java.util.Collection;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;

/**
 * Contains a static function used to find Definitions by a persistent class to make foreign relations.
 * @author Sander Benschop
 *
 */
public final class DefinitionFinder {

    /** Private constructor. */
    private DefinitionFinder() {
    }

    /**
     * Finds a Definition by a persistent class.
     * @param definitions Set of Definitions to search through
     * @param toFind Persistent class to look for in classDefinitions set
     * @return Found Definition
     */
    public static Definition findDefinitionByPersistentClass(Collection<Definition> definitions, Class<?> toFind) {
        for (Definition definition : definitions) {
            if (definition instanceof EntityDefinition) {
                EntityDefinition<?> entityDefinition = (EntityDefinition<?>) definition;
                if (entityDefinition.getDefinedClass() == toFind) {
                    return entityDefinition;
                }
            } else if (definition instanceof EmbeddableElementCollectionDefinition) {
                EmbeddableElementCollectionDefinition<?> embeddableElementCollectionDefinition = (EmbeddableElementCollectionDefinition<?>) definition;
                if (embeddableElementCollectionDefinition.getDefinedClass() == toFind) {
                    return embeddableElementCollectionDefinition;
                }
            }
        }
        return null;
    }
}
