package org.jarbframework.populator.excel.metamodel.generator;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceType;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;

public class ElementCollectionDefinitionGenerator {

    private final EntityManagerFactory entityManagerFactory;

    public ElementCollectionDefinitionGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Definition createDefinitionForSingleElementCollectionFromEntity(PropertyDefinition propertyDefinition) {
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(entityManagerFactory);
        Metamodel metamodel = entityManagerFactory.getMetamodel();

        if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.INVERSED_REFERENCE) {
            InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = propertyDefinition.getInverseJoinColumnReferenceProperties();
            InverseJoinColumnReferenceType inverseJoinColumnReferenceType = inverseJoinColumnReferenceProperties.getInverseJoinColumnReferenceType();

            switch (inverseJoinColumnReferenceType) {

            case EMBEDDABLE:
                EmbeddableType<?> embeddableType = inverseJoinColumnReferenceProperties.getEmbeddableType();
                Class<?> embeddableClass = embeddableType.getJavaType();
                EmbeddableElementCollectionDefinition.Builder<?> builder = EmbeddableElementCollectionDefinition.forClass(embeddableClass);
                builder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(metamodel.embeddable(embeddableClass), embeddableClass));
                return builder.build();
            case SERIALIZABLE_CLASS:
                break;
            default:
                throw new RuntimeException("Passed PropertyDefinition ' " + propertyDefinition.getName() + " ' is not an invesedJoinColumn of a known type.");
            }
        }
        return null;
    }

}
