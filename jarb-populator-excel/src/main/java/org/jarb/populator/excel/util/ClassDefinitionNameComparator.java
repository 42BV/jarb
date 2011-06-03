package org.jarb.populator.excel.util;

import java.io.Serializable;
import java.util.Comparator;

import org.jarb.populator.excel.metamodel.ClassDefinition;

/**
 * Compares two classDefinitions by name, this is used to sort lists of classDefinitions alphabetically.
 * Overrides the Comparator class
 * @author Sander Benschop
 *
 */
public final class ClassDefinitionNameComparator implements Comparator<ClassDefinition>, Serializable {

    private static final long serialVersionUID = -8304887998371258709L;

    /**
     * Compares two classDefinitions by name.
     * @param classDefinition1 The first classDefinition necessary for comparison
     * @param classDefinition2 The other necessary classDefinition
     * @return Negative integer if classDefinition2 > classDefinition1, positive integer if classDefinition1 > classDefinition2 and 0 if equal
     */
    @Override
    public int compare(ClassDefinition classDefinition1, ClassDefinition classDefinition2) {
        return classDefinition1.getTableName().compareTo(classDefinition2.getTableName());
    }
}
