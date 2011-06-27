package org.jarb.populator.excel.mapping.importer;

import java.util.Set;

import org.jarb.populator.excel.metamodel.EntityDefinition;


/**
 * Contains a static function used to find ClassDefinitions by a persistent class to make foreign relations.
 * @author Sander Benschop
 *
 */
public final class ClassDefinitionFinder {

    /** Private constructor. */
    private ClassDefinitionFinder() {
    }

    /**
     * Finds a classDefinition by a persistent class.
     * @param classDefinitions Set of classDefinitions to search through
     * @param toFind Persistent class to look for in classDefinitions set
     * @return Found ClassDefinition
     */
    public static EntityDefinition<?> findClassDefinitionByPersistentClass(Set<EntityDefinition<?>> classDefinitions, Class<?> toFind) {
        for (EntityDefinition<?> classDefinition : classDefinitions) {
            if (classDefinition.getEntityClass() == toFind) {
                return classDefinition;
            }
        }
        return null;
    }
}
