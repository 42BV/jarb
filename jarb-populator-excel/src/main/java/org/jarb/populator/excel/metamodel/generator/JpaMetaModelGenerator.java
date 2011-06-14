package org.jarb.populator.excel.metamodel.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Persistence API (JPA) implementation of {@link MetaModelGenerator}.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class JpaMetaModelGenerator implements MetaModelGenerator {
    private static final Logger logger = LoggerFactory.getLogger(JpaMetaModelGenerator.class);
    private final EntityManagerFactory entityManagerFactory;

    public JpaMetaModelGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generate() {
        return generateForTypes(entityManagerFactory.getMetamodel().getEntities());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MetaModel generateFor(java.lang.Class<?>... entityClasses) {
        return generateFor(Arrays.asList(entityClasses));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generateFor(Iterable<Class<?>> entityClasses) {
        Set<EntityType<?>> entityTypes = new HashSet<EntityType<?>>();
        for (Class<?> entityClass : entityClasses) {
            EntityType<?> entityType = entityType(entityClass);
            if (entityType != null) {
                entityTypes.add(entityType);
            }
        }
        return generateForTypes(entityTypes);
    }

    /**
     * Retrieve the {@link EntityType} for a specific entity class.
     * @param entityClass class of the entity to look for
     * @return matching entity type, or {@code null} if nothing can be found
     */
    private EntityType<?> entityType(Class<?> entityClass) {
        EntityType<?> entity = null;
        try {
            Metamodel jpaMetamodel = entityManagerFactory.getMetamodel();
            entity = jpaMetamodel.entity(entityClass);
        } catch (IllegalArgumentException e) {
            logger.warn("Class '{}' is not in the JPA meta model, ensure it is annotated as @Entity.", entityClass.getName());
        }
        return entity;
    }

    /**
     * Generate a new {@link MetaModel} for a specific selection of entity types.
     * @param entityTypes type of entities to include in our meta model
     * @return meta model including a definition for each valid entity type
     */
    protected MetaModel generateForTypes(Set<EntityType<?>> entityTypes) {
        MetaModel metamodel = new MetaModel();
        for (EntityType<?> entityType : entityTypes) {
            ClassDefinition<?> classDefinition = generateClassDefinition(entityType);
            if (classDefinition != null) {
                metamodel.addClassDefinition(classDefinition);
            }
        }
        return metamodel;
    }

    /**
     * Generate the {@link ClassDefinition} of a specific entity type.
     * @param entityType type of entity being inspected
     * @return definition of the class
     */
    private ClassDefinition<?> generateClassDefinition(EntityType<?> entityType) {
        try {
            return ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entityType, true);
        } catch (Exception e) {
            // TODO: Delegating class should not be throwing this many exceptions
            throw new RuntimeException(e);
        }
    }
}
