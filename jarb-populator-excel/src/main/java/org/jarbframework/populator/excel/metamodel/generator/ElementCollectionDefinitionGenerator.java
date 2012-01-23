package org.jarbframework.populator.excel.metamodel.generator;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.hssf.record.formula.functions.T;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceType;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;

public final class ElementCollectionDefinitionGenerator {

    public static Definition createDefinitionForSingleElementCollectionFromEntity(PropertyDefinition propertyDefinition,
            EntityManagerFactory entityManagerFactory) {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(entityManagerFactory);

        if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.INVERSED_REFERENCE) {
            InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = propertyDefinition.getInverseJoinColumnReferenceProperties();
            InverseJoinColumnReferenceType inverseJoinColumnReferenceType = inverseJoinColumnReferenceProperties.getInverseJoinColumnReferenceType();

            switch (inverseJoinColumnReferenceType) {

            case EMBEDDABLE:
                EmbeddableType<T> embeddableType = inverseJoinColumnReferenceProperties.getEmbeddableType();
                Class<T> embeddableClass = embeddableType.getJavaType();
                EmbeddableElementCollectionDefinition.Builder<T> builder = EmbeddableElementCollectionDefinition.forClass(embeddableClass);
                builder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(metamodel.embeddable(embeddableClass), embeddableClass));
                return builder.build();

            case SERIALIZABLE_CLASS:
                break;

            }
        }
        return null;
    }

}
