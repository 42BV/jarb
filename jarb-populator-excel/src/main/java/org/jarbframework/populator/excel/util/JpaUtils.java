package org.jarbframework.populator.excel.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.proxy.HibernateProxy;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceType;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * Java Persistence API (JPA) utilities.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public final class JpaUtils {

    private static final String ROW_IDENTIFIER_COLUMN_NAME = "#";

    /** Utility class, do not attempt to instantiate. */
    private JpaUtils() {
    }

    /**
     * Create a new {@link EntityManager}, using all configured properties.
     * @param entityManagerFactory factory that builds entity managers
     * @return new entity manager instance
     */
    public static EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager(entityManagerFactory.getProperties());
    }

    /**
     * Retrieve the identifier (@Id) value of an entity.
     * @param entity reference to the entity
     * @param entityManagerFactory entity manager factory
     * @return identifier of the entity, if any
     */
    public static Object getIdentifier(Object entity, EntityManagerFactory entityManagerFactory) {
        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entityManagerFactory.getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * Returns the defined class of passed definition if it possesses one.
     * @param definition Definition to get defined class from
     * @return Defined class
     */
    @Deprecated
    public static Class<?> getDefinedClassOfDefinition(Definition definition) {
        Class<?> definedClass = null;
        if (definition instanceof EntityDefinition<?>) {
            definedClass = ((EntityDefinition<?>) definition).getDefinedClass();
        } else if (definition instanceof ElementCollectionDefinition<?>) {
            definedClass = ((ElementCollectionDefinition<?>) definition).getDefinedClass();
        }
        return definedClass;
    }

    /**
     * Returns the table name of passed definition if it possesses one.
     * @param definition Definition to get table name from
     * @return Table name
     */
    @Deprecated
    public static String getTableNameOfDefinition(Definition definition) {
        String tableName = null;
        if (definition instanceof EntityDefinition<?>) {
            tableName = ((EntityDefinition<?>) definition).getTableName();
        } else if (definition instanceof ElementCollectionDefinition<?>) {
            tableName = ((ElementCollectionDefinition<?>) definition).getTableName();
        }
        return tableName;
    }

    /**
     * Returns the referred column names and the @JoinColumns names from a JPA annotated field. If no @JoinColumn names are present, the JPA default will be deduced.
     * @param schemaMapper JPA schemamapper used to retrieve the table name
     * @param owningEntity EntityType<?> of the Entity owning the passed field
     * @param field Field to get metadata from
     * @return HashMap with referred Column names as keys and @JoinColumn names as values
     */
    public static HashMap<String, String> getJoinColumnNamesFromJpaAnnotatedField(SchemaMapper schemaMapper, EntityType<?> owningEntity, Field field) {
        if (field.isAnnotationPresent(ElementCollection.class)) {
            return getJoinColumnNamesForElementCollectionField(schemaMapper, owningEntity, field);
        } else {
            return new HashMap<String, String>();
        }
    }

    /**
     * Returns the Column names belonging to an ElementCollection.
     * @param property Property which is annotated with @ElementCollection
     * @param metamodel Metamodel to retrieve additional data from
     * @return Set of Column names
     */
    public static HashSet<String> getElementCollectionColumnNames(PropertyDefinition property, MetaModel metamodel) {
        InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = property.getInverseJoinColumnReferenceProperties();
        HashSet<String> columnNames = new HashSet<String>();
        if (inverseJoinColumnReferenceProperties.getInverseJoinColumnReferenceType() == InverseJoinColumnReferenceType.EMBEDDABLE) {
            Class<?> definedClass = inverseJoinColumnReferenceProperties.getEmbeddableType().getJavaType();
            EmbeddableElementCollectionDefinition<?> embeddableElementCollectionDefinition = (EmbeddableElementCollectionDefinition<?>) metamodel
                    .elementCollection(definedClass);
            columnNames.addAll(embeddableElementCollectionDefinition.getColumnNames());
        }
        return columnNames;
    }

    /**
     * Returns the referenced column names and the @JoinColums names from an ElementCollection field.
     * @param schemaMapper JPA schemamapper used to retrieve the table name
     * @param owningEntity EntityType<?> of the Entity owning the passed field
     * @param field Field to get @JoinColumns from
     * @return HashMap with referred Column names as keys and @JoinColumn names as values
     */
    private static HashMap<String, String> getJoinColumnNamesForElementCollectionField(SchemaMapper schemaMapper, EntityType<?> owningEntity, Field field) {
        HashMap<String, String> joinColumnNames = new HashMap<String, String>();
        if (field.isAnnotationPresent(CollectionTable.class)) {
            joinColumnNames = createColumnNamesFromCollectionTableAnnotation(field);
        } else if (joinColumnNames.isEmpty()) {
            joinColumnNames = createColumnNamesByJPADefault(schemaMapper, owningEntity);
        }
        return joinColumnNames;
    }

    /**
     * Creates a HashMap with referenced column names as keys and @JoinColumn names as values, with data gathered from the @CollectionTable annotation.
     * @param field Field to get CollectionTable annotation from
     * @return HashMap with referred Column names as keys and @JoinColumn names as values
     */
    private static HashMap<String, String> createColumnNamesFromCollectionTableAnnotation(Field field) {
        HashMap<String, String> joinColumnNames = new HashMap<String, String>();
        CollectionTable collectionTable = field.getAnnotation(CollectionTable.class);

        if (collectionTable.joinColumns().length == 1) {
            JoinColumn joinColumn = collectionTable.joinColumns()[0];
            joinColumnNames.put(ROW_IDENTIFIER_COLUMN_NAME, joinColumn.name());
        } else {
            for (JoinColumn joinColumn : collectionTable.joinColumns()) {
                joinColumnNames.put(joinColumn.referencedColumnName(), joinColumn.name());
            }
        }

        return joinColumnNames;
    }

    /**
     * Creates a HashMap with referenced column names as keys and @JoinColumn names as values, from the JPA spec's defaults.
     * @param schemaMapper 
     * @param owningEntity 
     * @return HashMap with referred Column names as keys and @JoinColumn names as values
     */
    private static HashMap<String, String> createColumnNamesByJPADefault(SchemaMapper schemaMapper, EntityType<?> owningEntity) {
        HashMap<String, String> joinColumnNames = new HashMap<String, String>();
        Class<?> owningClass = owningEntity.getJavaType();
        String owningClassDatabaseTableName = schemaMapper.tableNameOf(owningClass);
        String elementCollectionDatabaseAttributeName = getIdentifierColumnName(owningEntity);
        String concatinatedJoinColumnName = owningClassDatabaseTableName + "_" + elementCollectionDatabaseAttributeName;
        joinColumnNames.put(ROW_IDENTIFIER_COLUMN_NAME, concatinatedJoinColumnName);
        return joinColumnNames;
    }

    /**
     * Returns the column name of the identifier field from the passed entity.
     * @param entity Entity to get the identifier field's column name from.
     * @return Identifier's column name
     */
    private static String getIdentifierColumnName(EntityType<?> entity) {
        for (SingularAttribute<?, ?> attribute : entity.getSingularAttributes()) {
            if (attribute.isId()) {
                return getJPAColumnNameOfSingularAttribute(attribute);
            }
        }
        throw new RuntimeException("Identifier field not found in entity '" + entity.getName() + "'");
    }

    /**
     * Returns the JPA Column name for passed SingularAttribute. If no name is annotated in the @Column annotation or the @Column annotation is not present,
     * the field name will be returned (per JPA2 spec default).
     * @param singularAttribute Singular Attribute to get the JPA column name from
     * @return JPA Column name for passed SingularAttribute
     */
    private static String getJPAColumnNameOfSingularAttribute(SingularAttribute<?, ?> singularAttribute) {
        String columnName = "";
        Field field = (Field) singularAttribute.getJavaMember();

        if (field.isAnnotationPresent(Column.class)) {
            columnName = field.getAnnotation(Column.class).name();
        }

        if (columnName.isEmpty()) {
            columnName = singularAttribute.getName();
        }
        return columnName;
    }
}
