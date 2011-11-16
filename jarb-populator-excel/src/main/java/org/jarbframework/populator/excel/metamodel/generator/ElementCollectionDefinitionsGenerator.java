package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;

import javax.persistence.CollectionTable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;

import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;

public class ElementCollectionDefinitionsGenerator {

    private final EntityManagerFactory entityManagerFactory;

    public ElementCollectionDefinitionsGenerator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public ElementCollectionDefinition<?> createSingleElementCollectionDefinitionFromMetamodel(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        return createElementcollectionDefinitionFromEmbeddable(embeddableType, enclosingType);
    }

    @SuppressWarnings("unchecked")
    private <T> ElementCollectionDefinition<T> createElementcollectionDefinitionFromEmbeddable(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        Class<T> embeddableClass = (Class<T>) embeddableType.getJavaType();
        Class<T> enclosingClass = (Class<T>) enclosingType.getJavaType();
        ElementCollectionDefinition.Builder<T> builder = ElementCollectionDefinition.forClass(embeddableClass);
        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
        builder.setTableName(getTableName(embeddableType, enclosingType));
        builder.setEnclosingClass(enclosingClass);
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(schemaMapper);
        builder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(embeddableType, enclosingType));
        return builder.build();
    }

    private String getTableName(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        String tableName = null;
        String fieldName = getFieldNameForEmbeddedType(embeddableType, enclosingType);
        if (fieldName != null) {
            try {
                tableName = deduceTableName(embeddableType, enclosingType, fieldName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tableName;
    }

    private String deduceTableName(EmbeddableType<?> embeddableType,
            EntityType<?> enclosingType, String fieldName)
            throws NoSuchFieldException {
        String tableName = getTableNameFromCollectionTableAnnotation(enclosingType, fieldName);
        if (tableName.isEmpty()) {
            tableName = JpaMetaModelUtils.createElementCollectionTableNameByJPADefault(enclosingType.getJavaType(), fieldName, entityManagerFactory);
        }
        return tableName;
    }

    private String getTableNameFromCollectionTableAnnotation(EntityType<?> enclosingType, String fieldName)
            throws NoSuchFieldException {
        String tableName = "";
        Annotation annotation = enclosingType.getJavaType().getDeclaredField(fieldName).getAnnotation(CollectionTable.class);
        if (annotation != null) {
            CollectionTable collectionTable = (CollectionTable) annotation;
            tableName = collectionTable.name();
        }
        return tableName;
    }

    private String getFieldNameForEmbeddedType(EmbeddableType<?> embeddableType, EntityType<?> enclosingType) {
        for (PluralAttribute<?, ?, ?> attribute : enclosingType.getPluralAttributes()) {
            if (attribute.getElementType() == embeddableType) {
                return attribute.getName();
            }
        }
        return null;
    }

}
