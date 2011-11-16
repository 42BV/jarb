package org.jarbframework.populator.excel.mapping.importer;

import java.util.Collection;

import org.jarbframework.populator.excel.metamodel.Definition;

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
     * Finds a classDefinition by a persistent class.
     * @param definitions Set of classDefinitions to search through
     * @param toFind Persistent class to look for in classDefinitions set
     * @return Found ClassDefinition
     */
    public static Definition<?> findDefinitionByPersistentClass(Collection<Definition<?>> definitions, Class<?> toFind) {
        for (Definition<?> definition : definitions) {
            if (definition.getDefinedClass() == toFind) {
                return definition;
            }
        }
        return null;
    }
}
