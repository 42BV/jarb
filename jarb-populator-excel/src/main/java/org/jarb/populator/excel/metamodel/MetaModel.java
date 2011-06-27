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
public class MetaModel implements Iterable<EntityDefinition<?>> {
    private final Map<Class<?>, EntityDefinition<?>> classDefinitionMap;
    
    /**
     * Construct a new {@link MetaModel}.
     * @param classDefinitions all class definitions
     */
    public MetaModel(Collection<EntityDefinition<?>> classDefinitions) {
        classDefinitionMap = new HashMap<Class<?>, EntityDefinition<?>>();
        for(EntityDefinition<?> classDefinition : classDefinitions) {
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
    public <T> EntityDefinition<? super T> entity(Class<T> persistentClass) {
        EntityDefinition<? super T> definition = (EntityDefinition<T>) classDefinitionMap.get(persistentClass);
        if(definition == null && persistentClass.getSuperclass() != null) {
            definition = entity(persistentClass.getSuperclass());
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
    public Collection<EntityDefinition<?>> getClassDefinitions() {
        return Collections.unmodifiableCollection(classDefinitionMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<EntityDefinition<?>> iterator() {
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
