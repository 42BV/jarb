package org.jarb.populator.excel.metamodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Describes the entities in our context.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MetaModel {
    private final SortedSet<ClassDefinition<?>> classDefinitions;
    
    /**
     * Construct a new {@link MetaModel}.
     * @param classDefinitions all class definitions
     */
    public MetaModel(ClassDefinition<?>... classDefinitions) {
        this(Arrays.asList(classDefinitions));
    }
    
    /**
     * Construct a new {@link MetaModel}.
     * @param classDefinitions all class definitions
     */
    public MetaModel(Collection<ClassDefinition<?>> classDefinitions) {
        this.classDefinitions = new TreeSet<ClassDefinition<?>>(new ClassDefinitionNameComparator());
        this.classDefinitions.addAll(classDefinitions);
    }

    /**
     * Retrieve all currently defined class definitions.
     * @return each defined class definition
     */
    public SortedSet<ClassDefinition<?>> getClassDefinitions() {
        return Collections.unmodifiableSortedSet(classDefinitions);
    }

    /**
     * Retrieve the class definition of a specific persistent class,
     * or {@code null} if no matching definition can be found.
     * @param persistentClass class that we are finding for
     * @return description of the provided class, else {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> ClassDefinition<T> getClassDefinition(Class<T> persistentClass) {
        ClassDefinition<T> result = null;
        for (ClassDefinition<?> classDefinition : classDefinitions) {
            if (persistentClass.equals(classDefinition.getPersistentClass())) {
                result = (ClassDefinition<T>) classDefinition;
                break;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return classDefinitions.toString();
    }

}
