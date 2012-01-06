package org.jarbframework.populator.excel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.proxy.HibernateProxy;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;

/**
 * Java Persistence API (JPA) utilities.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public final class JpaUtils {

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
    //TO-DO Remove this function and replace by proper Definition type structure.
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
    //TO-DO Remove this function and replace by proper Definition type structure.
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
     * Returns the @JoinColumns names from a JPA annotated field. If no @JoinColumn names are present, the JPA default will be deduced.
     * @param schemaMapper JPA schemamapper used to retrieve the table name
     * @param owningEntity EntityType<?> of the Entity owning the passed field
     * @param field Field to get @JoinColumns from
     * @return List of JoinColumn names
     */
    public static List<String> getJoinColumnNamesFromJpaAnnotatedField(SchemaMapper schemaMapper, EntityType<?> owningEntity, Field field) {
        if (field.isAnnotationPresent(ElementCollection.class)) {
            return getJoinColumnNamesForElementCollectionField(schemaMapper, owningEntity, field);
        } else if (field.isAnnotationPresent(OneToMany.class)) {
            //To implement:
            //return getJoinColumnNamesForOneToManyField(field);
            return new ArrayList<String>();
        } else {
            return new ArrayList<String>();
        }
    }

    /**
     * Returns the @JoinColums names from an ElementCollection field.
     * @param schemaMapper JPA schemamapper used to retrieve the table name
     * @param owningEntity EntityType<?> of the Entity owning the passed field
     * @param field Field to get @JoinColumns from
     * @return List of JoinColumn names
     */
    private static List<String> getJoinColumnNamesForElementCollectionField(SchemaMapper schemaMapper, EntityType<?> owningEntity, Field field) {
        List<String> joinColumnNames = new ArrayList<String>();
        if (field.isAnnotationPresent(CollectionTable.class)) {
            CollectionTable collectionTable = field.getAnnotation(CollectionTable.class);
            for (JoinColumn joinColumn : collectionTable.joinColumns()) {
                joinColumnNames.add(joinColumn.name());
            }
        }

        if (joinColumnNames.isEmpty()) {
            Class<?> owningClass = owningEntity.getJavaType();
            String owningClassDatabaseTableName = schemaMapper.tableNameOf(owningClass);
            String elementCollectionDatabaseAttributeName = getIdentifierColumnName(owningEntity);
            joinColumnNames.add(owningClassDatabaseTableName + "_" + elementCollectionDatabaseAttributeName);
        }
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
