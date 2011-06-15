package org.jarb.populator.excel.metamodel;

import java.util.Comparator;

/**
 * Compares class definitions ascending based on name.
 */
public final class ClassDefinitionNameComparator implements Comparator<ClassDefinition<?>> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(ClassDefinition<?> left, ClassDefinition<?> right) {
        return left.getTableName().compareTo(right.getTableName());
    }
    
}
