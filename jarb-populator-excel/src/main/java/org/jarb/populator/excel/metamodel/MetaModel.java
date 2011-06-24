package org.jarb.populator.excel.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Describes the entities in our context.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MetaModel implements Iterable<ClassDefinition<?>> {
    private final Map<Class<?>, ClassDefinition<?>> classDefinitionMap;
    
    /**
     * Construct a new {@link MetaModel}.
     * @param classDefinitions all class definitions
     */
    public MetaModel(Collection<ClassDefinition<?>> classDefinitions) {
        classDefinitionMap = new HashMap<Class<?>, ClassDefinition<?>>();
        for(ClassDefinition<?> classDefinition : classDefinitions) {
            classDefinitionMap.put(classDefinition.getEntityClass(), classDefinition);
        }
    }
    
    /**
     * Retrieve the class definition of a specific persistent class,
     * or {@code null} if no matching definition can be found.
     * @param persistentClass class that we are finding for
     * @return description of the provided class, else {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> ClassDefinition<? super T> describe(Class<T> persistentClass) {
        ClassDefinition<? super T> definition = (ClassDefinition<T>) classDefinitionMap.get(persistentClass);
        if(definition == null && persistentClass.getSuperclass() != null) {
            definition = describe(persistentClass.getSuperclass());
        }
        return definition;
    }
    
    /**
     * Determine if this meta model contains the described persistent class.
     * @param persistentClass class that we are looking for
     * @return {@code true} if it is contained, else {@code false}
     */
    public boolean contains(Class<?> persistentClass) {
        boolean found = classDefinitionMap.containsKey(persistentClass);
        if(!found && persistentClass.getSuperclass() != null) {
            found = contains(persistentClass.getSuperclass());
        }
        return found;
    }
    
    /**
     * Return all persistent classes known by this metamodel.
     * @return unique collection of all known classes
     */
    public Set<Class<?>> getKnownClasses() {
        return Collections.unmodifiableSet(classDefinitionMap.keySet());
    }
    
    /**
     * Retrieve an unmodifiable collection of all class definitions.
     * @return all class definitions stored in this metamodel
     */
    public Collection<ClassDefinition<?>> getClassDefinitions() {
        return Collections.unmodifiableCollection(classDefinitionMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<ClassDefinition<?>> iterator() {
        return classDefinitionMap.values().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return classDefinitionMap.keySet().toString();
    }

}
