package org.jarb.populator.excel.metamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes the entities in our context.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MetaModel {
    private Set<ClassDefinition> classDefinitions = new HashSet<ClassDefinition>();

    /**
     * Retrieve all currently defined class definitions.
     * @return each defined class definition
     */
    public Set<ClassDefinition> getClassDefinitions() {
        return Collections.unmodifiableSet(classDefinitions);
    }

    /**
     * Include a class definition to this meta model.
     * @param classDefinition new class definition to include
     */
    public void addClassDefinition(ClassDefinition classDefinition) {
        classDefinitions.add(classDefinition);
    }

    /**
     * Retrieve the class definition of a specific persistent class,
     * or {@code null} if no matching definition can be found.
     * @param persistentClass class that we are finding for
     * @return description of the provided class, else {@code null}
     */
    public ClassDefinition findClassDefinition(Class<?> persistentClass) {
        ClassDefinition result = null;
        for (ClassDefinition classDefinition : classDefinitions) {
            if (persistentClass.equals(classDefinition.getPersistentClass())) {
                result = classDefinition;
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
