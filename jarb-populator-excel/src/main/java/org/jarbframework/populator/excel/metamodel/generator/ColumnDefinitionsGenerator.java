package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a list of ColumnDefinitions belonging to a ClassDefinition.
 * @author Sander Benschop
 *
 */
public final class ColumnDefinitionsGenerator {
    private final Logger logger = LoggerFactory.getLogger(ColumnDefinitionsGenerator.class);

    private final RegularColumnGenerator regularColumnGenerator;
    private final EmbeddedColumnGenerator embeddedColumnGenerator;

    public ColumnDefinitionsGenerator(SchemaMapper schemaMapper) {
        regularColumnGenerator = new RegularColumnGenerator(schemaMapper);
        embeddedColumnGenerator = new EmbeddedColumnGenerator(schemaMapper);
    }

    /**
     * Creates a list of columnDefinitions from an entity originated in the JPA meta-model.
     * @param subclassEntities Set of subclass entities
     * @param type Entity whose attributes will be added as ColumnDefinitions.
     * @param persistentClass Persistent class of the ClassDefinition
     * @return List of ColumnDefinitions
     */
    public List<PropertyDefinition> createPropertyDefinitions(Set<EntityType<?>> subclassEntities, EntityType<?> type, Class<?> persistentClass) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();
        addAttributesAsColumnDefinitions(columnDefinitions, type, persistentClass);
        createSuperTypeColumnDefinitions(columnDefinitions, type, persistentClass);
        if (!subclassEntities.isEmpty()) {
            createSubClassColumnDefinitions(columnDefinitions, subclassEntities);
        }
        return columnDefinitions;
    }

    public List<PropertyDefinition> createPropertyDefinitions(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        Class<?> embeddableClass = embeddableType.getJavaType();
        Class<?> enclosingClass = enclosingType.getJavaType();
        addAttributesAsColumnDefinitions(propertyDefinitions, embeddableType, embeddableClass, enclosingClass);
        return propertyDefinitions;
    }

    private void addAttributesAsColumnDefinitions(List<PropertyDefinition> columnDefinitions, ManagedType<?> type, Class<?> entityClass) {
        addAttributesAsColumnDefinitions(columnDefinitions, type, entityClass, null);
    }

    private void addAttributesAsColumnDefinitions(
    		List<PropertyDefinition> columnDefinitions, ManagedType<?> type, Class<?> entityClass, Class<?> enclosingClass) {
        for (Attribute<?, ?> attribute : type.getDeclaredAttributes()) {
            Field field = (Field) attribute.getJavaMember();

            List<PropertyDefinition> newlyGeneratedColumnDefinitions = createPropertyDefinitionList(
                    entityClass, enclosingClass, field);

            for (PropertyDefinition columnDefinition : newlyGeneratedColumnDefinitions) {
                addPropertyDefinitionIfUnique(columnDefinitions, type, columnDefinition);
            }
        }
    }

    private List<PropertyDefinition> createPropertyDefinitionList(
            Class<?> entityClass, Class<?> enclosingClass, Field field) {
        return createColumnsForField(field, entityClass, enclosingClass);
    }

    private List<PropertyDefinition> createColumnsForField(Field field, Class<?> entityClass, Class<?> enclosingClass) {
        List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();

        PropertyReference propertyReference = createPropertyReference(field, entityClass, enclosingClass);

        if ((field.getAnnotation(javax.persistence.Embedded.class) != null)) {
            columnDefinitions.addAll(embeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(propertyReference));
        } else {
            PropertyDefinition columnDefinition = regularColumnGenerator.createColumnDefinitionForRegularField(propertyReference);
            if (columnDefinition != null) {
                columnDefinitions.add(columnDefinition);
            }
        }
        return columnDefinitions;
    }

    private PropertyReference createPropertyReference(Field field,
            Class<?> entityClass, Class<?> enclosingClass) {
        PropertyReference propertyReference = null;
        if (enclosingClass == null) {
            propertyReference = new PropertyReference(entityClass, field.getName());
        } else {
            propertyReference = new PropertyReference(entityClass, enclosingClass, field.getName());
        }
        return propertyReference;
    }

    private void createSuperTypeColumnDefinitions(List<PropertyDefinition> columnDefinitions, IdentifiableType<?> type, Class<?> entityClass) {
        IdentifiableType<?> superType = type.getSupertype();
        if (superType != null) {
            addAttributesAsColumnDefinitions(columnDefinitions, superType, entityClass);
            createSuperTypeColumnDefinitions(columnDefinitions, superType, entityClass);
        }
    }

    private void createSubClassColumnDefinitions(List<PropertyDefinition> columnDefinitions, Set<EntityType<?>> subclassEntities) {
        for (EntityType<?> subEntity : subclassEntities) {
            addAttributesAsColumnDefinitions(columnDefinitions, subEntity, subEntity.getJavaType());
        }
    }

    private void addPropertyDefinitionIfUnique(List<PropertyDefinition> columnDefinitions, ManagedType<?> type, PropertyDefinition columnDefinition) {
        if (propertyDefinitionUnique(columnDefinitions, columnDefinition)) {
            columnDefinitions.add(columnDefinition);
        } else {
            logger.info("Duplicate property definition '" + columnDefinition.getColumnName() + "' in entity '" + type + "' was ommited.");
            logger.info("Possibly the column name is defined in several subclasses. Since these are merged into one the column names must be unique.");
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

}
