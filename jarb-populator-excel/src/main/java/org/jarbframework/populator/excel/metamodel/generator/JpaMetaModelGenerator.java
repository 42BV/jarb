package org.jarbframework.populator.excel.metamodel.generator;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;
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
     * @param entityManagerFactory entity manager factory, used for its meta-model
     */
    public JpaMetaModelGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generate() {
        Collection<Definition<?>> entities = new HashSet<Definition<?>>();
        for (EntityType<?> entityType : JpaMetaModelUtils.getRootEntities(entityManagerFactory.getMetamodel())) {
            entities.add(describeEntity(entityType));

            for (EmbeddableType<?> elementCollection : JpaMetaModelUtils.getElementCollectionsForEntity(entityType)) {
                entities.add(describeElementCollection(elementCollection, entityType));
            }

        }
        return new MetaModel(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generateFor(Collection<Class<?>> entityClasses) {
        Collection<Definition<?>> entities = new HashSet<Definition<?>>();
        for (Class<?> entityClass : entityClasses) {
            EntityType<?> entityType = entityManagerFactory.getMetamodel().entity(entityClass);
            entities.add(describeEntity(entityType));
        }
        return new MetaModel(entities);
    }

    /**
     * Generate the {@link EntityDefinition} of a specific entity type.
     * @param entityType type of entity being inspected
     * @return definition of the class
     */
    private EntityDefinition<?> describeEntity(EntityType<?> entityType) {
        try {
            LOGGER.debug("Generating metamodel definition of entity class '{}'...", entityType.getJavaType().getName());
            return new EntityDefinitionsGenerator(entityManagerFactory).createSingleEntityDefinitionFromMetamodel(entityType, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ElementCollectionDefinition<?> describeElementCollection(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        LOGGER.debug("Generating metamodel definition of embeddable class '{}'...", embeddableType.getJavaType().getName());
        return new ElementCollectionDefinitionsGenerator(entityManagerFactory).createSingleElementCollectionDefinitionFromMetamodel(embeddableType,
                enclosingType);
    }

}
