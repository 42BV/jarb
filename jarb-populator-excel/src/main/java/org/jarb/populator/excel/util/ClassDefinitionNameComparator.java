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
     * @param left The first classDefinition necessary for comparison
     * @param right The other necessary classDefinition
     * @return Negative integer if classDefinition2 > classDefinition1, positive integer if classDefinition1 > classDefinition2 and 0 if equal
     */
    @Override
    public int compare(ClassDefinition left, ClassDefinition right) {
        return left.getTableName().compareTo(right.getTableName());
    }
}
