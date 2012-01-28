package org.jarbframework.populator.excel.metamodel.generator;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
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
        Collection<EntityDefinition<?>> entities = new HashSet<EntityDefinition<?>>();
        Collection<Definition> elementCollections = new HashSet<Definition>();
        for (EntityType<?> entityType : JpaMetaModelUtils.getRootEntities(entityManagerFactory.getMetamodel())) {
            EntityDefinition<?> entityDefinition = describeEntity(entityType);
            entities.add(entityDefinition);
            elementCollections.addAll(returnDefinitionsForElementCollectionsFromEntity(entityDefinition, entityManagerFactory));

        }
        return new MetaModel(entities, elementCollections);
    }

    /**
     * Retrieves all ElementCollectionDefinitions that are connected with a certain EntityDefinition.
     * @param entityDefinition EntityDefinition to search ElementCollectionDefinition occurences of
     * @param entityManagerFactory JPA's EntityManagerFactory needed for creating ColumnDefinitions
     * @return Collection of Definitions from ElementCollections
     */
    private Collection<Definition> returnDefinitionsForElementCollectionsFromEntity(EntityDefinition<?> entityDefinition,
            EntityManagerFactory entityManagerFactory) {
        Collection<Definition> definitions = new HashSet<Definition>();
        ElementCollectionDefinitionGenerator elementCollectionDefinitionGenerator = new ElementCollectionDefinitionGenerator(entityManagerFactory);
        for (PropertyDefinition propertyDefinition : entityDefinition.properties()) {
            Definition definition = elementCollectionDefinitionGenerator.createDefinitionForSingleElementCollectionFromEntity(propertyDefinition);
            if (definition != null) {
                definitions.add(definition);
            }
        }
        return definitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModel generateFor(Collection<Class<?>> entityClasses) {
        Collection<EntityDefinition<?>> entities = new HashSet<EntityDefinition<?>>();
        Collection<Definition> elementCollections = new HashSet<Definition>();
        for (Class<?> entityClass : entityClasses) {
            EntityType<?> entityType = entityManagerFactory.getMetamodel().entity(entityClass);
            EntityDefinition<?> entityDefinition = describeEntity(entityType);
            entities.add(entityDefinition);
            elementCollections.addAll(returnDefinitionsForElementCollectionsFromEntity(entityDefinition, entityManagerFactory));
        }
        return new MetaModel(entities, elementCollections);
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
}
