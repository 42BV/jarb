package org.jarbframework.populator.excel.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jarbframework.populator.excel.util.JpaUtils;

/**
 * Describes the entities in our context.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MetaModel implements Iterable<Definition> {
    private final Map<Class<?>, Definition> entityDefinitionsMap;
    private final Map<Class<?>, EmbeddableElementCollectionDefinition<?>> embeddableElementCollectionDefinitionMap;
    
    /**
     * Construct a new {@link MetaModel}.
     * @param classDefinitions all class definitions
     */
    public MetaModel(Collection<EntityDefinition<?>> classDefinitions) {
        entityDefinitionsMap = new HashMap<Class<?>, Definition>();
        embeddableElementCollectionDefinitionMap = new HashMap<Class<?>, EmbeddableElementCollectionDefinition<?>>();
        
        for (EntityDefinition<?> classDefinition : classDefinitions) {
            entityDefinitionsMap.put(JpaUtils.getDefinedClassOfDefinition(classDefinition), classDefinition);   
        }

    }

    /**
     * Retrieve the entity definition of a specific persistent class,
     * or {@code null} if no matching definition can be found.
     * @param entityClass class that we are finding for
     * @return description of the provided class, else {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> EntityDefinition<? super T> entity(Class<T> entityClass) {
        EntityDefinition<? super T> definition = (EntityDefinition<T>) entityDefinitionsMap.get(entityClass);
        if (definition == null && entityClass.getSuperclass() != null) {
            definition = entity(entityClass.getSuperclass());
        }
        return definition;
    }

    /**
     * Determine if this meta model contains the described persistent class.
     * @param entityClass class that we are looking for
     * @return {@code true} if it is contained, else {@code false}
     */
    public boolean contains(Class<?> entityClass) {
        boolean found = entityDefinitionsMap.containsKey(entityClass);
        if (!found && entityClass.getSuperclass() != null) {
            found = contains(entityClass.getSuperclass());
        }
        return found;
    }

    /**
     * Retrieve an unmodifiable collection of all entity
     * types currently described in this meta model.
     * @return definition of each entity type
     */
    public Collection<Definition> entities() {
        return Collections.unmodifiableCollection(entityDefinitionsMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Definition> iterator() {
        return entityDefinitionsMap.values().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entityDefinitionsMap.keySet().toString();
    }

}
