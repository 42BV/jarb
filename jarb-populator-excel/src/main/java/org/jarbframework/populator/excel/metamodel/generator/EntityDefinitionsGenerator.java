package org.jarbframework.populator.excel.metamodel.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;

/**
 * Class which is responsible for generating a list of ready-to-be-used ClassDefinitions containing columns, a persistent class, etc.
 * Generates these from JPA's metamodel.
 * @author Sander Benschop
 *
 */
public class EntityDefinitionsGenerator {
    private final EntityManagerFactory entityManagerFactory;

    public EntityDefinitionsGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Used to create a ClassDefinition, is used by the function createClassDefinitionsFromMetamodel but can also be used to create a single ClassDefinition.
     * This can be useful for unit testing.
     * Persistent class must be annotated as @Entity in order for this to work.
     * @param entity An entity from the meta-model.
     * @param includeSubClasses Whether or not to include subclasses in the ClassDefinition
     */
    public EntityDefinition<?> createSingleEntityDefinitionFromMetamodel(EntityType<?> entity, boolean includeSubClasses) {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        if (includeSubClasses) {
            subClassEntities = SubclassRetriever.getSubClassEntities(entity, entities);
        }
        return createEntityDefinitionFromEntity(entity, subClassEntities);
    }

    /**
     * Calls the function to create a new basic classDefinition and then adds columnDefinitions and a subclass mapping.
     * @param entity Entity to create a classDefinition from
     * @param entities All entities from the Metamodel
     * @param subClassEntities All subclasses of the Entity
     */
    @SuppressWarnings("unchecked")
    private <T> EntityDefinition<T> createEntityDefinitionFromEntity(EntityType<T> entity, Set<EntityType<?>> subClassEntities) {
        final Class<T> entityClass = entity.getJavaType();
        EntityDefinition.Builder<T> builder = EntityDefinition.forClass(entityClass);
        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
        builder.setTableName(schemaMapper.tableNameOf(entityClass));
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(entityManagerFactory);
        builder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, entityClass));
        if (!subClassEntities.isEmpty()) {
            builder.setDiscriminatorColumnName(DiscriminatorColumnGenerator.getDiscriminatorColumnName(entityClass));
            for (Map.Entry<String, Class<?>> subClassMapping : SubclassRetriever.getSubClassMapping(subClassEntities).entrySet()) {
                builder.includeSubClass(subClassMapping.getKey(), (Class<? extends T>) subClassMapping.getValue());
            }
        }
        return builder.build();
    }

}
