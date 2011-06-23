package org.jarb.populator.excel.metamodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes the entities in our context.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MetaModel {
    private final Map<Class<?>, ClassDefinition<?>> classDefinitionMap;
    
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
        classDefinitionMap = new HashMap<Class<?>, ClassDefinition<?>>();
        for(ClassDefinition<?> classDefinition : classDefinitions) {
            classDefinitionMap.put(classDefinition.getPersistentClass(), classDefinition);
        }
    }

    /**
     * Retrieve all currently defined class definitions.
     * @return each defined class definition
     */
    public Collection<ClassDefinition<?>> getClassDefinitions() {
        return Collections.unmodifiableCollection(classDefinitionMap.values());
    }

    /**
     * Retrieve the class definition of a specific persistent class,
     * or {@code null} if no matching definition can be found.
     * @param persistentClass class that we are finding for
     * @return description of the provided class, else {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> ClassDefinition<? super T> describeClass(Class<T> persistentClass) {
        ClassDefinition<? super T> definition = (ClassDefinition<T>) classDefinitionMap.get(persistentClass);
        if(definition == null && persistentClass.getSuperclass() != null) {
            definition = describeClass(persistentClass.getSuperclass());
        }
        return definition;
    }
    
    /**
     * Determine if this meta model contains the described persistent class.
     * @param persistentClass class that we are looking for
     * @return {@code true} if it is contained, else {@code false}
     */
    public boolean containsClass(Class<?> persistentClass) {
        boolean found = classDefinitionMap.containsKey(persistentClass);
        if(!found && persistentClass.getSuperclass() != null) {
            found = containsClass(persistentClass.getSuperclass());
        }
        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return classDefinitionMap.keySet().toString();
    }

}
