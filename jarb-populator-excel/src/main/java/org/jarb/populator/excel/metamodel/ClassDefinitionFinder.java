package org.jarb.populator.excel.metamodel;

import java.util.Set;


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
    public static ClassDefinition findClassDefinitionByPersistentClass(Set<ClassDefinition> classDefinitions, Class<?> toFind) {
        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition.getPersistentClass() == toFind) {
                return classDefinition;
            }
        }
        return null;
    }
}
