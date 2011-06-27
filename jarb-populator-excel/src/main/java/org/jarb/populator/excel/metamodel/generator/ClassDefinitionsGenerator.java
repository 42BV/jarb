package org.jarb.populator.excel.metamodel.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.utils.database.JpaMetaModelUtils;

/**
 * Class which is responsible for generating a list of ready-to-be-used ClassDefinitions containing columns, a persistent class, etc.
 * Generates these from JPA's metamodel.
 * @author Sander Benschop
 *
 */
public class ClassDefinitionsGenerator {

    /**
     * Used to create a ClassDefinition, is used by the function createClassDefinitionsFromMetamodel but can also be used to create a single ClassDefinition.
     * This can be useful for unittesting.
     * Persistent class must be annotated as @Entity in order for this to work.
     * @param entityManagerFactory EntityManagerFactory from the ApplicationContext file
     * @param entity An entity from the metamodel.
     * @param includeSubClasses Whether or not to include subclasses in the ClassDefinition
     * @return ClassDefinition, still without a worksheetDefinition 
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public static EntityDefinition<?> createSingleClassDefinitionFromMetamodel(EntityManagerFactory entityManagerFactory, EntityType<?> entity,
            boolean includeSubClasses) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        Metamodel metamodel = entityManagerFactory.getMetamodel();

        Set<EntityType<?>> entities = metamodel.getEntities();
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();

        if (includeSubClasses) {
            subClassEntities = SubclassRetriever.getSubClassEntities(entity, entities);
        }

        return createClassDefinitionFromEntity(entity, entities, subClassEntities);
    }

    /**
     * Calls the function to create a new basic classDefinition and then adds columnDefinitions and a subclass mapping.
     * @param entity Entity to create a classDefinition from
     * @param entities All entities from the Metamodel
     * @param subClassEntities All subclasses of the Entity
     * @return ClassDefinition with columns and subclass mapping
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    @SuppressWarnings("unchecked")
    private static <T> EntityDefinition<T> createClassDefinitionFromEntity(EntityType<T> entity, Set<EntityType<?>> entities, Set<EntityType<?>> subClassEntities)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<T> entityClass = entity.getJavaType();
        EntityDefinition.Builder<T> builder = EntityDefinition.forClass(entityClass);
        builder.setTableName(JpaMetaModelUtils.getTableName(entityClass));
        builder.includeProperties(ColumnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, entityClass));
        if(!subClassEntities.isEmpty()) {
            builder.setDiscriminatorColumnName(DiscriminatorColumnGenerator.getDiscriminatorColumnName(entityClass));
            for(Map.Entry<String, Class<?>> subClassMapping : SubclassRetriever.getSubClassMapping(subClassEntities).entrySet()) {
                builder.includeSubClass(subClassMapping.getKey(), (Class<? extends T>) subClassMapping.getValue());
            }
        }
        return builder.build();
    }
    
    /** Private constructor. */
    private ClassDefinitionsGenerator() {
    }

}
