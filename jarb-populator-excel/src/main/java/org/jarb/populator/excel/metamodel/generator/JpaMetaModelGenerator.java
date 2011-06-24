package org.jarb.populator.excel.metamodel.generator;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
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
        Collection<ClassDefinition<?>> classDefinitions = new HashSet<ClassDefinition<?>>();
        for (EntityType<?> entityType : entityManagerFactory.getMetamodel().getEntities()) {
            ClassDefinition<?> classDefinition = describeEntityType(entityType);
            if (classDefinition != null) {
                classDefinitions.add(classDefinition);
            }
        }
        return new MetaModel(classDefinitions);
    }

    /**
     * Generate the {@link ClassDefinition} of a specific entity type.
     * @param entityType type of entity being inspected
     * @return definition of the class
     */
    private ClassDefinition<?> describeEntityType(EntityType<?> entityType) {
        try {
            LOGGER.debug("Generating metamodel definition of '{}'.", entityType.getJavaType().getName());
            return ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entityType, true);
        } catch (Exception e) {
            // TODO: Delegating class should not be throwing this many exceptions
            throw new RuntimeException(e);
        }
    }
}
