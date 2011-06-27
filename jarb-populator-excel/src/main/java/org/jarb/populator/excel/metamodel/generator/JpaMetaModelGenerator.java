package org.jarb.populator.excel.metamodel.generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.utils.database.JpaMetaModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Persistence API (JPA) implementation of {@link MetaModelGenerator}.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class JpaMetaModelGenerator implements MetaModelGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaMetaModelGenerator.class);
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Construct a new {@link JpaMetaModelGenerator}.
     * @param entityManagerFactory entity manager factory, used for its metamodel
     */
    public JpaMetaModelGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generate() {
        Collection<EntityDefinition<?>> classDefinitions = new HashSet<EntityDefinition<?>>();
        for (EntityType<?> entityType : getRootEntities()) {
            classDefinitions.add(describeEntityType(entityType));
        }
        return new MetaModel(classDefinitions);
    }
    
    private Collection<EntityType<?>> getRootEntities() {
        Set<EntityType<?>> entityTypes = new HashSet<EntityType<?>>();
        for(EntityType<?> entityType : entityManagerFactory.getMetamodel().getEntities()) {
            if(!hasEntitySuperClass(entityType.getJavaType())) {
                entityTypes.add(entityType);
            }
        }
        return entityTypes;
    }
    
    private boolean hasEntitySuperClass(Class<?> entityClass) {
        boolean found = false;
        Class<?> currentClass = entityClass;
        while(currentClass.getSuperclass() != null) {
            final Class<?> superClass = currentClass.getSuperclass();
            if(JpaMetaModelUtils.isEntity(superClass)) {
                found = true;
                break;
            }
            currentClass = superClass;
        }
        return found;
    }

    /**
     * Generate the {@link EntityDefinition} of a specific entity type.
     * @param entityType type of entity being inspected
     * @return definition of the class
     */
    private EntityDefinition<?> describeEntityType(EntityType<?> entityType) {
        try {
            LOGGER.debug("Generating metamodel definition of '{}'.", entityType.getJavaType().getName());
            return ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entityType, true);
        } catch (Exception e) {
            // TODO: Delegating class should not be throwing this many exceptions
            throw new RuntimeException(e);
        }
    }
}
