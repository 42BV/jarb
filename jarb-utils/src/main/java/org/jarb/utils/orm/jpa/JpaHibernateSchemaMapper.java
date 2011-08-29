/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm.jpa;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarb.utils.BeanAnnotationUtils.getAnnotation;
import static org.jarb.utils.BeanAnnotationUtils.hasAnnotation;
import static org.jarb.utils.ReflectionUtils.getFieldType;
import static org.jarb.utils.orm.jpa.JpaMetaModelUtils.assertIsEntity;
import static org.jarb.utils.orm.jpa.JpaMetaModelUtils.findRootEntityClass;
import static org.jarb.utils.orm.jpa.JpaMetaModelUtils.getIdentifierPropertyName;
import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jarb.utils.orm.ColumnReference;
import org.jarb.utils.orm.SchemaMapper;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class JpaHibernateSchemaMapper implements SchemaMapper {
    private static final String NAMING_STRATEGY_KEY = "hibernate.ejb.naming_strategy";

    /** Naming strategy used to determine the eventual mapping. **/
    private final NamingStrategy namingStrategy;

    public JpaHibernateSchemaMapper() {
        this(new DefaultNamingStrategy());
    }

    public JpaHibernateSchemaMapper(NamingStrategy namingStrategy) {
        Assert.notNull(namingStrategy, "Naming strategy property is required.");
        this.namingStrategy = namingStrategy;
    }

    public JpaHibernateSchemaMapper(EntityManagerFactory entityManagerFactory) {
        Object namingStrategyClassName = entityManagerFactory.getProperties().get(NAMING_STRATEGY_KEY);
        if (namingStrategyClassName == null) {
            namingStrategy = new DefaultNamingStrategy();
        } else {
            Assert.isInstanceOf(String.class, namingStrategyClassName, "Naming strategy needs to be a string based class name.");
            namingStrategy = instantiateStrategy(namingStrategyClassName.toString());
        }
    }

    private NamingStrategy instantiateStrategy(String className) {
        Class<?> namingStrategyClass;
        try {
            namingStrategyClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Naming strategy '" + className + "' is not available in the classpath.", e);
        }
        return (NamingStrategy) instantiateClass(namingStrategyClass);
    }

    @Override
    public String table(Class<?> entityClass) {
        assertIsEntity(entityClass);

        Class<?> tableClass = entityClass;
        Class<?> rootEntityClass = findRootEntityClass(entityClass);
        Inheritance inheritance = rootEntityClass.getAnnotation(Inheritance.class);
        if (inheritance != null && inheritance.strategy() == SINGLE_TABLE) {
            tableClass = rootEntityClass;
        }
        return readTableName(tableClass);
    }

    @Override
    public ColumnReference column(Class<?> entityClass, String propertyName) {
        assertIsEntity(entityClass);

        ColumnReference columnReference = null;
        if (hasColumn(entityClass, propertyName)) {
            String tableName = tableForProperty(entityClass, propertyName);
            String columnName = columnName(entityClass, propertyName);
            columnReference = new ColumnReference(tableName, columnName);
        }
        return columnReference;
    }

    /**
     * Determine if the bean property should map to a database column.
     * Whenever the property is annotated as @Transient, or is a collection
     * based reference, we return {@code false}.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property, as declared
     * @return {@code true} if a column is expected, else {@code false}
     */
    private boolean hasColumn(Class<?> entityClass, String propertyName) {
        return !(hasAnnotation(entityClass, propertyName, Transient.class) || isCollectionReference(entityClass, propertyName));
    }

    private boolean isCollectionReference(Class<?> entityClass, String propertyName) {
        return hasAnnotation(entityClass, propertyName, OneToMany.class) || hasAnnotation(entityClass, propertyName, ManyToMany.class);
    }

    /**
     * Retrieve the table name for a specific bean property.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property, as declared
     * @return name of the table
     */
    private String tableForProperty(Class<?> entityClass, String propertyName) {
        Class<?> tableClass = entityClass;
        Class<?> rootEntityClass = findRootEntityClass(entityClass);
        Inheritance inheritance = rootEntityClass.getAnnotation(Inheritance.class);
        if (inheritance != null) {
            switch (inheritance.strategy()) {
            case SINGLE_TABLE:
                tableClass = rootEntityClass;
                break;
            case JOINED:
                Field field = ReflectionUtils.findField(entityClass, propertyName);
                Assert.notNull(field, "Could not find property '" + propertyName + "' for " + entityClass.getClass().getSimpleName());
                tableClass = field.getDeclaringClass();
                break;
            }
        }
        return readTableName(tableClass);
    }

    private String readTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && isNotBlank(table.name())) {
            return namingStrategy.tableName(table.name());
        } else {
            return readEntityName(entityClass);
        }
    }

    private String readEntityName(Class<?> entityClass) {
        String entityName;
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (isNotBlank(entity.name())) {
            entityName = entity.name();
        } else {
            entityName = entityClass.getSimpleName();
        }
        return namingStrategy.classToTableName(entityName);
    }

    /**
     * Retrieve the column name for a specific bean property.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property, as declared
     * @return name of the column
     */
    private String columnName(Class<?> entityClass, String propertyName) {
        if (hasAnnotation(entityClass, propertyName, ManyToOne.class)) {
            return readReferenceColumnName(entityClass, propertyName);
        } else {
            return readColumnName(entityClass, propertyName);
        }
    }

    private String readReferenceColumnName(Class<?> entityClass, String propertyName) {
        JoinColumn joinColumn = getAnnotation(entityClass, propertyName, JoinColumn.class);
        if (joinColumn != null && isNotBlank(joinColumn.name())) {
            return namingStrategy.columnName(joinColumn.name());
        } else {
            Class<?> targetClass = getTargetEntityClass(entityClass, propertyName);
            ColumnReference targetColumn = column(targetClass, getIdentifierPropertyName(targetClass));
            return namingStrategy.foreignKeyColumnName(propertyName, targetClass.getName(), targetColumn.getTableName(), targetColumn.getColumnName());
        }
    }

    private Class<?> getTargetEntityClass(Class<?> entityClass, String propertyName) {
        ManyToOne manyToOne = getAnnotation(entityClass, propertyName, ManyToOne.class);
        if (manyToOne.targetEntity() != void.class) {
            return manyToOne.targetEntity();
        } else {
            return getFieldType(entityClass, propertyName);
        }
    }

    private String readColumnName(Class<?> entityClass, String propertyName) {
        Column column = getAnnotation(entityClass, propertyName, Column.class);
        if (column != null && isNotBlank(column.name())) {
            return namingStrategy.columnName(column.name());
        } else {
            return namingStrategy.propertyToColumnName(propertyName);
        }
    }

}
