package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.orm.SchemaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a list of ColumnDefinitions belonging to a ClassDefinition.
 * @author Sander Benschop
 *
 */
public final class ColumnDefinitionsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnDefinitionsGenerator.class);
    private final RegularColumnGenerator regularColumnGenerator;
    private final EmbeddedColumnGenerator embeddedColumnGenerator;

    public ColumnDefinitionsGenerator(SchemaMapper schemaMapper) {
        regularColumnGenerator = new RegularColumnGenerator(schemaMapper);
        embeddedColumnGenerator = new EmbeddedColumnGenerator(schemaMapper);
    }

    /**
     * Creates a list of columnDefinitions from an entity originated in the JPA meta-model.
     * @param subclassEntities Set of subclass entities
     * @param entity Entity whose attributes will be added as ColumnDefinitions.
     * @param persistentClass Persistent class of the ClassDefinition
     * @return List of ColumnDefinitions
     */
    public List<PropertyDefinition> createPropertyDefinitions(Set<EntityType<?>> subclassEntities, EntityType<?> entity, Class<?> persistentClass) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        addAttributesAsColumnDefinitions(columnDefinitions, entity, persistentClass);
        createSuperTypeColumnDefinitions(columnDefinitions, entity, persistentClass);
        if (!subclassEntities.isEmpty()) {
            createSubClassColumnDefinitions(columnDefinitions, subclassEntities, persistentClass);
        }
        return columnDefinitions;
    }

    private void addAttributesAsColumnDefinitions(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type, Class<?> entityClass) {
        for (Attribute<?, ?> attribute : type.getDeclaredAttributes()) {
            Field field = (Field) attribute.getJavaMember();
            List<PropertyDefinition> newlyGeneratedColumnDefinitions = createColumnsForField(field, entityClass);
            for (PropertyDefinition columnDefinition : newlyGeneratedColumnDefinitions) {
                addPropertyDefinitionIfUnique(columnDefinitions, type, columnDefinition);
            }
        }
    }

    private void createSuperTypeColumnDefinitions(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type, Class<?> entityClass) {
        IdentifiableType<?> superType = type.getSupertype();
        if (superType != null) {
            addAttributesAsColumnDefinitions(columnDefinitions, superType, entityClass);
            createSuperTypeColumnDefinitions(columnDefinitions, superType, entityClass);
        }
    }

    private void createSubClassColumnDefinitions(List<PropertyDefinition> columnDefinitions, Set<EntityType<?>> subclassEntities, Class<?> persistentClass) {
        for (EntityType<?> subEntity : subclassEntities) {
            addAttributesAsColumnDefinitions(columnDefinitions, subEntity, persistentClass);
        }
    }

    private void addPropertyDefinitionIfUnique(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type, PropertyDefinition columnDefinition) {
        if (propertyDefinitionUnique(columnDefinitions, columnDefinition)) {
            columnDefinitions.add(columnDefinition);
        } else {
            LOGGER.warn("Duplicate property definition '" + columnDefinition.getColumnName() + "' in entity '" + type + "' was ommited.");
            LOGGER.warn("Possibly the column name is defined in several subclasses. Since these are merged into one the column names must be unique.");
        }
    }

    private boolean propertyDefinitionUnique(List<PropertyDefinition> propertyDefs, PropertyDefinition newPropertyDef) {
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

    private List<PropertyDefinition> createColumnsForField(Field field, Class<?> entityClass) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        if ((field.getAnnotation(javax.persistence.Embedded.class) != null)) {
            columnDefinitions.addAll(embeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(field, entityClass));
        } else {
            PropertyDefinition columnDefinition = regularColumnGenerator.createColumnDefinitionForRegularField(field, entityClass);
            if (columnDefinition != null) {
                columnDefinitions.add(columnDefinition);
            }
        }
        return columnDefinitions;
    }

}
