package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceType;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.utils.bean.BeanProperties;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;

/**
 * Creates a ColumnDefinition from a field.
 * @author Sander Benschop
 */
public class FieldAnalyzer {
    private final EntityManagerFactory entityManagerFactory;

    public FieldAnalyzer(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public PropertyDefinition.Builder analyzeField(PropertyReference propertyReference) {
        PropertyDefinition.Builder columnDefinitionBuilder = null;
        Field field = BeanProperties.findPropertyField(propertyReference);
        if (isCollection(field)) {
            if (field.isAnnotationPresent(JoinTable.class)) {
                JoinTable joinTable = field.getAnnotation(JoinTable.class);
                columnDefinitionBuilder = joinTableDefinition(joinTable, field);
            } else if (field.isAnnotationPresent(ElementCollection.class)) {
                columnDefinitionBuilder = inversedReferencePropertyDefinition(field);
            }
        } else {
            SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
            String referencedColumnName = schemaMapper.getColumnReference(propertyReference).getColumnName();

            if (!referencedColumnName.isEmpty()) {
                columnDefinitionBuilder = PropertyDefinition.forField(field).setColumnName(referencedColumnName);
                if (isAssociation(field)) {
                    columnDefinitionBuilder.setDatabaseType(PropertyDatabaseType.REFERENCE);
                }
                if (isGeneratedValue(field)) {
                    columnDefinitionBuilder.valueIsGenerated();
                }
                if (isIdColumn(field)) {
                    columnDefinitionBuilder.columnIsIdColumn();
                }
            }
        }
        return columnDefinitionBuilder;
    }

    private boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private boolean isAssociation(Field field) {
        return field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null;
    }

    private boolean isGeneratedValue(Field field) {
        return field.getAnnotation(GeneratedValue.class) != null;
    }

    private boolean isIdColumn(Field field) {
        return field.getAnnotation(Id.class) != null;
    }

    private PropertyDefinition.Builder joinTableDefinition(JoinTable annotation, Field field) {
        String joinColumnName = null;
        if (annotation.joinColumns().length != 0) {
            joinColumnName = annotation.joinColumns()[0].name();
        } else {
            throw new UnsupportedOperationException("JoinTable annotations with implicit JoinColumns are not yet supported by JaRB.");
        }
        String inverseJoinColumnName = null;
        if (annotation.inverseJoinColumns().length != 0) {
            inverseJoinColumnName = annotation.inverseJoinColumns()[0].name();
        } else {
            throw new UnsupportedOperationException("JoinTable annotations with implicit JoinColumns are not yet supported by JaRB.");
        }
        return PropertyDefinition.forField(field).setDatabaseType(PropertyDatabaseType.COLLECTION_REFERENCE).setJoinTableName(annotation.name())
                .setJoinColumnName(joinColumnName).setInverseJoinColumnName(inverseJoinColumnName);
    }

    private PropertyDefinition.Builder inversedReferencePropertyDefinition(Field field) {
        PropertyDefinition.Builder propertyDefinition = PropertyDefinition.forField(field);
        propertyDefinition.setDatabaseType(PropertyDatabaseType.INVERSED_REFERENCE);
        propertyDefinition.setInverseJoinColumnReferenceProperties(inverseJoinColumnReferenceProperties(field));
        return propertyDefinition;
    }

    /**
     * Creates an InversedJoinColumnReferenceProperties instance which holds the referred class (unless it refers to a basic typed class) 
     * as well as the JoinColumnColumn names.
     * @param field Field for which an InversedJoinColumnReference will be created
     * @return InversedJoinColumnReferenceProperties instance
     */
    private InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties(Field field) {
        InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = new InverseJoinColumnReferenceProperties();
        Map<String, String> joinColumnNames = getJpaJoinColumnNamesForInversedReferenceField(field);
        inverseJoinColumnReferenceProperties.setJoinColumnNames(joinColumnNames);
        String tableName = JpaMetaModelUtils.createTableNameForElementCollection(field, field.getDeclaringClass(), entityManagerFactory);
        inverseJoinColumnReferenceProperties.setReferencedTableName(tableName);
        addPropertiesForEmbeddableElementCollection(field, inverseJoinColumnReferenceProperties);
        return inverseJoinColumnReferenceProperties;
    }

    /**
     * Returns a hashmap with the referred column names as keys and the Jpa annotated JoinColumn names on the field as values.
     * If the necessary annotations are not present, the JPA2 spec's default will be applied
     * @param field Field to get the names from
     * @return Map with the referred column names as keys and the Jpa annotated JoinColumn names as values
     */
    private Map<String, String> getJpaJoinColumnNamesForInversedReferenceField(Field field) {
        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
        EntityType<?> entityType = entityManagerFactory.getMetamodel().entity(field.getDeclaringClass());
        return JpaUtils.getJoinColumnNamesFromJpaAnnotatedField(schemaMapper, entityType, field);
    }

    /**
     * Sets the Referenced class and reference type if the InversedReference is an EmbeddableElementCollection.
     * @param field Field to check the type arguments of
     * @param inverseJoinColumnReferenceProperties Properties datastructure to mutate
     */
    private void addPropertiesForEmbeddableElementCollection(Field field, InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();

        for (Type actualType : type.getActualTypeArguments()) {
            Class<?> typeClass = (Class<?>) actualType;
            if (JpaMetaModelUtils.isEmbeddable(typeClass)) {
                EmbeddableType<?> embeddableType = entityManagerFactory.getMetamodel().embeddable(typeClass);
                inverseJoinColumnReferenceProperties.setEmbeddableType(embeddableType);
                inverseJoinColumnReferenceProperties.setInverseJoinColumnReferenceType(InverseJoinColumnReferenceType.EMBEDDABLE);
                break;
            }
        }
    }

}
