package org.jarb.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which is responsible for generating a list of ready-to-be-used ClassDefinitions containing columns, a persistent class, etc.
 * Generates these from JPA's metamodel.
 * @author Sander Benschop
 *
 */
public final class ClassDefinitionsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDefinitionsGenerator.class);

    /** Private constructor. */
    private ClassDefinitionsGenerator() {
    }

    /**
     * Used to create a list of all ClassDefinitions that can be made from the metamodel.
     * Classes that need to be persisted should be annotated with @Entity, otherwise they will not be picked up.
     * @param entityManagerFactory EntityManagerFactory from the ApplicationContext file
     * @return List of ClassDefinitions, still without a worksheetDefinition 
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public static List<ClassDefinition<?>> createClassDefinitionsFromMetamodel(EntityManagerFactory entityManagerFactory) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();

        return createClassDefinitionList(entityManagerFactory, entities);
    }

    /**
     * Creates a list of ClassDefinitions by calling the createSingleClassDefinitionFromMetamodel function several times.
     * @param entityManagerFactory EntityManagerFactory needed to gather the metamodel from
     * @param entities List of entities from the metamodel
     * @return List of ClassDefinitions
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static List<ClassDefinition<?>> createClassDefinitionList(EntityManagerFactory entityManagerFactory, Set<EntityType<?>> entities)
            throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        List<ClassDefinition<?>> classDefinitionList = new ArrayList<ClassDefinition<?>>();
        for (EntityType<?> entity : entities) {
            ClassDefinition<?> classDefinition = createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, true);
            if (classDefinition != null) {
                classDefinitionList.add(classDefinition);
            }
        }
        return classDefinitionList;
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
    public static ClassDefinition<?> createSingleClassDefinitionFromMetamodel(EntityManagerFactory entityManagerFactory, EntityType<?> entity,
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
    private static ClassDefinition<?> createClassDefinitionFromEntity(EntityType<?> entity, Set<EntityType<?>> entities, Set<EntityType<?>> subClassEntities)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassDefinition<?> classDefinition = null;
        if (entity != null) {
            Class<?> persistentClass = Class.forName(entity.getName());
            classDefinition = createBasicClassDefinition(entities, entity, persistentClass);
            if (classDefinition != null) {
                classDefinition.addColumnDefinitionList(ColumnDefinitionsGenerator.createColumnDefinitions(subClassEntities, entity, persistentClass));
                classDefinition.addSubClassMap(SubclassRetriever.getSubClassMapping(subClassEntities));
            }
        }
        return classDefinition;
    }

    /**
     * Create a basic ClassDefinition with a tablename and persistent class.
     * @param entities Entities from the metamodel
     * @param entity Entity that will be made into a ClassDefinition
     * @param persistentClass Persistent class
     * @return ClassDefinition with a tablename and persistent class
     */
    private static ClassDefinition<?> createBasicClassDefinition(Set<EntityType<?>> entities, EntityType<?> entity, Class<?> persistentClass) {
        ClassDefinition<?> classDefinition = null;
        //See if Entity has got an @Table annotation
        if (hasTableAnnotation(persistentClass)) {
            //Create new ClassDefinition, enter table name from annotation, enter persistent class.
            classDefinition = newClassDefinition(persistentClass.getAnnotation(Table.class).name(), persistentClass);
        } else if (SuperclassRetriever.getSuperClassEntity(entity, entities) == null) {
            classDefinition = tryToGetTableNameFromEntityAnnotation(persistentClass);
        }
        return classDefinition;
    }

    /**
     * Tries to get the desired table name from an @Entity(name="table_name") annotation. If not present, the SimpleClassName is used.
     * @param persistentClass Persistent class
     * @return String with either the @Entity(name=...) value or the SimpleClassName
     */
    private static ClassDefinition<?> tryToGetTableNameFromEntityAnnotation(Class<?> persistentClass) {
        ClassDefinition<?> classDefinition;
        // ^^ Checks if Entity is subclass of another Entity in the model. If it is it will be implemented by superclass.
        //It could still have an @Entity name annotation, check this. Otherwise we'll take the simple class name.
        if (hasEntityNameAnnotation(persistentClass)) {
            classDefinition = newClassDefinition(persistentClass.getAnnotation(Entity.class).name(), persistentClass);
        } else {
            //Create new ClassDefinition, use persistent classname as tablename, enter persistent class.
            classDefinition = newClassDefinition(persistentClass.getSimpleName(), persistentClass);
        }
        return classDefinition;
    }

    /**
     * Returns the requested entity from JPA's metamodel.
     * @param persistentClass Persistent class necessary to get the entity from the metamodel
     * @param metamodel JPA's metamodel
     * @return EntityType from JPA's metamodel
     */
    public static EntityType<?> getEntityFromMetamodel(Class<?> persistentClass, Metamodel metamodel) {
        EntityType<?> entity = null;
        try {
            entity = metamodel.entity(persistentClass);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Class " + persistentClass.getName() + " is not in JPA's metamodel, check if it's annotated as @Entity.");
        }
        return entity;
    }

    /**
     * Creates a new ClassDefinition with a table name and a persistent class.
     * @param tableName Tablename
     * @param persistentClass Persistent class
     * @return ClassDefinition with a table name and persistent class
     */
    private static <T> ClassDefinition<T> newClassDefinition(String tableName, Class<T> persistentClass) {
        ClassDefinition<T> classDefinition = new ClassDefinition<T>(persistentClass);
        classDefinition.setTableName(tableName);
        return classDefinition;
    }

    /**
     * Adds WorksheetDefinitions to the ClassDefinitions in the list.
     * @param classDefinitions List of ClassDefinitions
     * @param excel Excel file needed to add WorksheetDefinitions
     */
    public static void addWorksheetDefinitionsToClassDefinitions(Collection<ClassDefinition<?>> classDefinitions, Workbook excel) {
        for (ClassDefinition<?> classDefinition : classDefinitions) {
            addSingleWorksheetDefinitionToClassDefinition(classDefinition, excel);
        }
    }

    /**
     * Adds a WorksheetDefinition to the ClassDefinition.
     * @param classDefinition ClassDefinition to add a WorksheetDefinition to
     * @param excel Excelfile to find the columns in.
     */
    public static void addSingleWorksheetDefinitionToClassDefinition(ClassDefinition<?> classDefinition, Workbook excel) {
        WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
    }

    /**
     * Checks if a persistent class has an @Table annotation.
     * @param persistentClass Persistent class 
     * @return True if @Table annotation is present
     */
    private static boolean hasTableAnnotation(Class<?> persistentClass) {
        for (Annotation annotation : persistentClass.getAnnotations()) {
            if (annotation.annotationType().equals(Table.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a persistent class has an @Table annotation.
     * @param persistentClass Persistent class 
     * @return True if @Table annotation is present
     */
    private static boolean hasEntityNameAnnotation(Class<?> persistentClass) {
        Boolean returnvalue = false;
        for (Annotation annotation : persistentClass.getAnnotations()) {
            if (annotation.annotationType().equals(Entity.class)) {
                returnvalue = isEntityNameValid(annotation);
            }
        }
        return returnvalue;
    }

    /**
     * Checks if an @Entity annotation holds a valid table name.
     * @param annotation @Entity annotation
     * @return True if table name is not null and not ""
     */
    private static boolean isEntityNameValid(Annotation annotation) {
        Entity annotatedEntity = (Entity) annotation;
        if ((annotatedEntity.name() != null) && (!annotatedEntity.name().isEmpty())) {
            return true;
        }
        return false;
    }
}
