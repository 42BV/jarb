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
public final class ClassDefinitionsGenerator {
    /** Private constructor. */
    private ClassDefinitionsGenerator() {
    }

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
        Class<T> persistentClass = (Class<T>) Class.forName(entity.getName());
        EntityDefinition.Builder<T> classDefinitionBuilder = createBasicClassDefinition(entities, entity, persistentClass);
        classDefinitionBuilder.includeProperties(ColumnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        if(!subClassEntities.isEmpty()) {
            classDefinitionBuilder.setDiscriminatorColumnName(DiscriminatorColumnGenerator.getDiscriminatorColumnName(persistentClass));
            for(Map.Entry<String, Class<?>> subClassMapping : SubclassRetriever.getSubClassMapping(subClassEntities).entrySet()) {
                classDefinitionBuilder.includeSubClass(subClassMapping.getKey(), (Class<? extends T>) subClassMapping.getValue());
            }
        }
        return classDefinitionBuilder.build();
    }

    /**
     * Create a basic ClassDefinition with a tablename and persistent class.
     * @param entities Entities from the metamodel
     * @param entity Entity that will be made into a ClassDefinition
     * @param persistentClass Persistent class
     * @return ClassDefinition with a tablename and persistent class
     */
    private static <T> EntityDefinition.Builder<T> createBasicClassDefinition(Set<EntityType<?>> entities, EntityType<T> entity, Class<T> persistentClass) {
        EntityDefinition.Builder<T> classDefinitionBuilder = EntityDefinition.forClass(persistentClass);
        classDefinitionBuilder.setTableName(JpaMetaModelUtils.getTableName(persistentClass));
        return classDefinitionBuilder;
    }

//    /**
//     * Tries to get the desired table name from an @Entity(name="table_name") annotation. If not present, the SimpleClassName is used.
//     * @param persistentClass Persistent class
//     * @return String with either the @Entity(name=...) value or the SimpleClassName
//     */
//    private static <T> ClassDefinition.Builder<T> tryToGetTableNameFromEntityAnnotation(Class<T> persistentClass) {
//        // ^^ Checks if Entity is subclass of another Entity in the model. If it is it will be implemented by superclass.
//        //It could still have an @Entity name annotation, check this. Otherwise we'll take the simple class name.
//        if (hasEntityNameAnnotation(persistentClass)) {
//            return newClassDefinition(persistentClass.getAnnotation(Entity.class).name(), persistentClass);
//        } else {
//            //Create new ClassDefinition, use persistent classname as tablename, enter persistent class.
//            return newClassDefinition(persistentClass.getSimpleName(), persistentClass);
//        }
//    }
//
//    /**
//     * Checks if a persistent class has an @Table annotation.
//     * @param persistentClass Persistent class 
//     * @return True if @Table annotation is present
//     */
//    private static boolean hasEntityNameAnnotation(Class<?> persistentClass) {
//        Boolean returnvalue = false;
//        for (Annotation annotation : persistentClass.getAnnotations()) {
//            if (annotation.annotationType().equals(Entity.class)) {
//                returnvalue = isEntityNameValid(annotation);
//            }
//        }
//        return returnvalue;
//    }
//
//    /**
//     * Checks if an @Entity annotation holds a valid table name.
//     * @param annotation @Entity annotation
//     * @return True if table name is not null and not ""
//     */
//    private static boolean isEntityNameValid(Annotation annotation) {
//        Entity annotatedEntity = (Entity) annotation;
//        if ((annotatedEntity.name() != null) && (!annotatedEntity.name().isEmpty())) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Checks if a persistent class has an @Table annotation.
//     * @param persistentClass Persistent class 
//     * @return True if @Table annotation is present
//     */
//    private static boolean hasTableAnnotation(Class<?> persistentClass) {
//        for (Annotation annotation : persistentClass.getAnnotations()) {
//            if (annotation.annotationType().equals(Table.class)) {
//                return true;
//            }
//        }
//        return false;
//    }

}
