package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
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
    public static List<PropertyDefinition> createPropertyDefinitions(Set<EntityType<?>> subclassEntities, EntityType<?> entity, Class<?> persistentClass)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
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
    private static void addAttributesAsColumnDefinitions(List<PropertyDefinition> columnDefinitions, EntityType<?> entity) throws InstantiationException,
            IllegalAccessException {
        for (Attribute<?, ?> attribute : entity.getDeclaredAttributes()) {
            Field field = (Field) attribute.getJavaMember();
            List<PropertyDefinition> newlyGeneratedColumnDefinitions = createColumnsForField(field);
            for (PropertyDefinition columnDefinition : newlyGeneratedColumnDefinitions) {
                addPropertyDefinitionIfUnique(columnDefinitions, entity, columnDefinition);
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
    private static void createSubClassColumnDefinitions(List<PropertyDefinition> columnDefinitions, Set<EntityType<?>> subclassEntities,
            Class<?> persistentClass) throws InstantiationException, IllegalAccessException {
        for (EntityType<?> subEntity : subclassEntities) {
            addAttributesAsColumnDefinitions(columnDefinitions, subEntity);
        }
    }

    /**
     * Checks if the columnnames are unique for this table and if it is, adds the newly generated ColumnDefinition to the list.
     * @param columnDefinitions List of columnDefinitions that are already defined.
     * @param entity Entity the columnDefinitions were generated from.
     * @param columnDefinition ColumnDefinition to add.
     */
    private static void addPropertyDefinitionIfUnique(List<PropertyDefinition> columnDefinitions, EntityType<?> entity, PropertyDefinition columnDefinition) {
        if (propertyDefinitionUnique(columnDefinitions, columnDefinition)) {
            columnDefinitions.add(columnDefinition);
        } else {
            LOGGER.warn("Duplicate property definition '" + columnDefinition.getColumnName() + "' in entity '" + entity.getName() + "' was ommited.");
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
    private static boolean propertyDefinitionUnique(List<PropertyDefinition> columnDefinitions, PropertyDefinition newColumnDefinition) {
        boolean unique = true;
        if(newColumnDefinition.getColumnName() != null) {
            for (PropertyDefinition columnDefinition : columnDefinitions) {
                if (columnDefinition.getColumnName() != null && columnDefinition.getColumnName().equals(newColumnDefinition.getColumnName())) {
                    unique = false;
                    break; // Can no longer become unique
                }
            }
        }
        return unique;
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
    private static List<PropertyDefinition> createColumnsForField(Field field) throws InstantiationException, IllegalAccessException {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        if ((field.getAnnotation(javax.persistence.Embedded.class) != null)) {
            columnDefinitions.addAll(EmbeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(field));
        } else {
            PropertyDefinition columnDefinition = RegularColumnGenerator.createColumnDefinitionForRegularField(field);
            if (columnDefinition != null) {
                columnDefinitions.add(columnDefinition);
            }
        }
        return columnDefinitions;
    }
}
