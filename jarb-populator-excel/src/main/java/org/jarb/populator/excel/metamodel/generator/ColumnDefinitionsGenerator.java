package org.jarb.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a list of ColumnDefinitions belonging to a ClassDefinition.
 * @author Sander Benschop
 *
 */
public final class ColumnDefinitionsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnDefinitionsGenerator.class);

    /** Private constructor. */
    private ColumnDefinitionsGenerator() {
    }

    /**
     * Creates a list of columnDefinitions from an entity originated in the JPA metamodel.
     * @param subclassEntities Set of subclass entities
     * @param entity Entity whose attributes will be added as ColumnDefinitions.
     * @param persistentClass Persistent class of the ClassDefinition
     * @return List of ColumnDefinitions
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public static List<ColumnDefinition> createColumnDefinitions(Set<EntityType<?>> subclassEntities, EntityType<?> entity, Class<?> persistentClass)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
        //Add attributes as ColumnDefinitions to ClassDefinition
        addAttributesAsColumnDefinitions(columnDefinitions, entity);
        if (!subclassEntities.isEmpty()) {
            //Add attributes of subclass and the sub's persistent class.
            createSubClassColumnDefinitions(columnDefinitions, subclassEntities, persistentClass);
        }
        return columnDefinitions;
    }

    /**
     * Returns a list of ColumnDefinitions from either regular or embedded attributes.
     * @param entity Entity whose attributes will be added as ColumnDefinitions
     * @return List of ColumnDefinitions
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static void addAttributesAsColumnDefinitions(List<ColumnDefinition> columnDefinitions, EntityType<?> entity) throws InstantiationException,
            IllegalAccessException {
        for (Attribute<?, ?> attribute : entity.getDeclaredAttributes()) {
            Field field = (Field) attribute.getJavaMember();
            List<ColumnDefinition> newlyGeneratedColumnDefinitions = createColumnsForField(field);
            for (ColumnDefinition columnDefinition : newlyGeneratedColumnDefinitions) {
                addColumnDefinitionIfUnique(columnDefinitions, entity, columnDefinition);
            }
        }
    }

    /**
     * Creates a list of ColumnDefinitions from all subclasses of the persistent class.
     * @param subclassEntities List of subclass entities
     * @param persistentClass Persistent class
     * @param columnDefinitions List of columnDefinitions 
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static void createSubClassColumnDefinitions(List<ColumnDefinition> columnDefinitions, Set<EntityType<?>> subclassEntities,
            Class<?> persistentClass) throws InstantiationException, IllegalAccessException {
        for (EntityType<?> subEntity : subclassEntities) {
            addAttributesAsColumnDefinitions(columnDefinitions, subEntity);
        }
        columnDefinitions.add(DiscriminatorColumnGenerator.createDiscriminatorColumnDefinition(persistentClass));
    }

    /**
     * Checks if the columnnames are unique for this table and if it is, adds the newly generated ColumnDefinition to the list.
     * @param columnDefinitions List of columnDefinitions that are already defined.
     * @param entity Entity the columnDefinitions were generated from.
     * @param columnDefinition ColumnDefinition to add.
     */
    private static void addColumnDefinitionIfUnique(List<ColumnDefinition> columnDefinitions, EntityType<?> entity, ColumnDefinition columnDefinition) {
        if (columnNameUnique(columnDefinitions, columnDefinition)) {
            columnDefinitions.add(columnDefinition);
        } else {
            LOGGER.warn("Duplicate column name '" + columnDefinition.getColumnName() + "' in entity '" + entity.getName() + "' was ommited.");
            LOGGER.warn("Possibly the column name is defined in several subclasses. Since these are merged into one the column names must be unique.");
        }
    }

    /**
     * Checks if the ColumnDefinition newColumnDefinitions has a columnName that is already in the ColumnDefinitions list.
     * If so, it will not be added.
     * @param columnDefinitions List of current columnDefinitions
     * @param newColumnDefinition Newly generated columnDefinition
     * @return True if unique, false if not.
     */
    private static boolean columnNameUnique(List<ColumnDefinition> columnDefinitions, ColumnDefinition newColumnDefinition) {
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getColumnName().equals(newColumnDefinition.getColumnName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a field has got an @Embedded annotation or not. 
     * Then calls the proper function to create either one ColumnDefinition in the case of a regular attribute. 
     * If it's an embedded attribute it could be several.
     * @param field Field to generate the ColumnDefinition(s) from
     * @return Set of ColumnDefinition(s)
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static List<ColumnDefinition> createColumnsForField(Field field) throws InstantiationException, IllegalAccessException {
        List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
        if ((field.getAnnotation(javax.persistence.Embedded.class) != null)) {
            columnDefinitions.addAll(EmbeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(field));
        } else {
            ColumnDefinition columnDefinition = RegularColumnGenerator.createColumnDefinitionForRegularField(field);
            if (columnDefinition != null) {
                columnDefinitions.add(columnDefinition);
            }
        }
        return columnDefinitions;
    }
}
