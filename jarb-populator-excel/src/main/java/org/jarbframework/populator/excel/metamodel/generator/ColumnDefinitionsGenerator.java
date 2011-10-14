package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;

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
        // Add attributes as ColumnDefinitions to ClassDefinition
        addAttributesAsColumnDefinitions(columnDefinitions, entity);
        createSuperTypeColumnDefinitions(columnDefinitions, entity);
        if (!subclassEntities.isEmpty()) {
            // Add attributes of subclass and the sub's persistent class.
            createSubClassColumnDefinitions(columnDefinitions, subclassEntities, persistentClass);
        }
        return columnDefinitions;
    }

    private static void addAttributesAsColumnDefinitions(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type) {
        for (Attribute<?, ?> attribute : type.getDeclaredAttributes()) {
            Field field = (Field) attribute.getJavaMember();
            List<PropertyDefinition> newlyGeneratedColumnDefinitions = createColumnsForField(field);
            for (PropertyDefinition columnDefinition : newlyGeneratedColumnDefinitions) {
                addPropertyDefinitionIfUnique(columnDefinitions, type, columnDefinition);
            }
        }
    }

    private static void createSuperTypeColumnDefinitions(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type) {
        IdentifiableType<?> superType = type.getSupertype();
        if (superType != null) {
            addAttributesAsColumnDefinitions(columnDefinitions, superType);
            createSuperTypeColumnDefinitions(columnDefinitions, superType);
        }
    }

    private static void createSubClassColumnDefinitions(List<PropertyDefinition> columnDefinitions, Set<EntityType<?>> subclassEntities,
            Class<?> persistentClass) throws InstantiationException, IllegalAccessException {
        for (EntityType<?> subEntity : subclassEntities) {
            addAttributesAsColumnDefinitions(columnDefinitions, subEntity);
        }
    }

    private static void addPropertyDefinitionIfUnique(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type, PropertyDefinition columnDefinition) {
        if (propertyDefinitionUnique(columnDefinitions, columnDefinition)) {
            columnDefinitions.add(columnDefinition);
        } else {
            LOGGER.warn("Duplicate property definition '" + columnDefinition.getColumnName() + "' in entity '" + type + "' was ommited.");
            LOGGER.warn("Possibly the column name is defined in several subclasses. Since these are merged into one the column names must be unique.");
        }
    }

    private static boolean propertyDefinitionUnique(List<PropertyDefinition> propertyDefs, PropertyDefinition newPropertyDef) {
        boolean unique = true;
        if (newPropertyDef.getColumnName() != null) {
            for (PropertyDefinition columnDefinition : propertyDefs) {
                if (columnDefinition.getColumnName() != null && columnDefinition.getColumnName().equals(newPropertyDef.getColumnName())) {
                    unique = false;
                    break; // Can no longer become unique
                }
            }
        }
        return unique;
    }

    private static List<PropertyDefinition> createColumnsForField(Field field) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        try {
            if ((field.getAnnotation(javax.persistence.Embedded.class) != null)) {
                columnDefinitions.addAll(EmbeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(field));
            } else {
                PropertyDefinition columnDefinition = RegularColumnGenerator.createColumnDefinitionForRegularField(field);
                if (columnDefinition != null) {
                    columnDefinitions.add(columnDefinition);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return columnDefinitions;
    }

}
