/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm.jpa;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarb.utils.ConditionChecker.hasText;
import static org.jarb.utils.ConditionChecker.instanceOf;
import static org.jarb.utils.ConditionChecker.notNull;
import static org.jarb.utils.ReflectionUtils.getFieldNames;
import static org.jarb.utils.ReflectionUtils.getFieldType;
import static org.jarb.utils.orm.jpa.JpaMetaModelUtils.assertIsEntity;
import static org.jarb.utils.orm.jpa.JpaMetaModelUtils.findRootEntityClass;
import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jarb.utils.bean.BeanAnnotationScanner;
import org.jarb.utils.bean.BeanAnnotationScannerImpl;
import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.ColumnReference;
import org.jarb.utils.orm.SchemaMapper;
import org.springframework.util.ReflectionUtils;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class JpaHibernateSchemaMapper implements SchemaMapper {
    private static final String NAMING_STRATEGY_PROP_KEY = "hibernate.ejb.naming_strategy";

    /**
     * Retrieves the annotations of a bean class.
     */
    private final BeanAnnotationScanner annotationScanner = new BeanAnnotationScannerImpl(true, false);

    /**
     * Naming strategy used to determine the eventual mapping.
     */
    private final NamingStrategy namingStrategy;

    public JpaHibernateSchemaMapper() {
        this(new DefaultNamingStrategy());
    }

    public JpaHibernateSchemaMapper(NamingStrategy namingStrategy) {
        this.namingStrategy = notNull(namingStrategy, "Naming strategy property is required.");
    }

    public JpaHibernateSchemaMapper(EntityManagerFactory entityManagerFactory) {
        Object namingStrategyClassName = entityManagerFactory.getProperties().get(NAMING_STRATEGY_PROP_KEY);
        if (namingStrategyClassName == null) {
            namingStrategy = new DefaultNamingStrategy();
        } else {
            namingStrategy = instantiateStrategy(instanceOf(namingStrategyClassName, String.class, "Naming strategy property should be a string."));
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

    // Table naming

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

    // Column naming

    @Override
    public ColumnReference column(PropertyReference propertyReference) {
        assertIsEntity(propertyReference.getBeanClass());

        ColumnReference columnReference = null;
        if (hasColumn(propertyReference)) {
            String tableName = tableForProperty(propertyReference);
            String columnName = columnName(propertyReference);
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
    private boolean hasColumn(PropertyReference propertyReference) {
        return !(annotationScanner.hasAnnotation(propertyReference, Transient.class) || isCollectionReference(propertyReference));
    }

    private boolean isCollectionReference(PropertyReference propertyReference) {
        return annotationScanner.hasAnnotation(propertyReference, OneToMany.class) || annotationScanner.hasAnnotation(propertyReference, ManyToMany.class);
    }

    /**
     * Retrieve the table name for a specific bean property.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property, as declared
     * @return name of the table
     */
    private String tableForProperty(PropertyReference propertyReference) {
        Class<?> tableClass = propertyReference.getBeanClass();
        Class<?> rootEntityClass = findRootEntityClass(tableClass);
        Inheritance inheritance = rootEntityClass.getAnnotation(Inheritance.class);
        if (inheritance != null) {
            switch (inheritance.strategy()) {
            case SINGLE_TABLE:
                tableClass = rootEntityClass;
                break;
            case JOINED:
                Field field = ReflectionUtils.findField(propertyReference.getBeanClass(), propertyReference.getName());
                tableClass = notNull(field, "Could not find property '" + propertyReference + "'.").getDeclaringClass();
                break;
            }
        }
        return readTableName(tableClass);
    }

    /**
     * Retrieve the column name for a specific bean property.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property, as declared
     * @return name of the column
     */
    private String columnName(PropertyReference propertyReference) {
        if (annotationScanner.hasAnnotation(propertyReference, ManyToOne.class)) {
            return readReferenceColumnName(propertyReference);
        } else {
            return readColumnName(propertyReference);
        }
    }

    private String readReferenceColumnName(PropertyReference propertyReference) {
        JoinColumn joinColumn = annotationScanner.findAnnotation(propertyReference, JoinColumn.class);
        if (joinColumn != null && isNotBlank(joinColumn.name())) {
            return namingStrategy.columnName(joinColumn.name());
        } else {
            Class<?> referencedEntityClass = getTargetEntityClass(propertyReference);
            String referencedPropertyName = getIdentifierPropertyName(referencedEntityClass);
            ColumnReference referencedColumn = column(new PropertyReference(referencedPropertyName, referencedEntityClass));
            return namingStrategy.foreignKeyColumnName(propertyReference.getName(), referencedEntityClass.getName(), referencedColumn.getTableName(),
                    referencedColumn.getColumnName());
        }
    }

    private String getIdentifierPropertyName(Class<?> entityClass) {
        String identifierPropertyName = null;
        for (String propertyName : getFieldNames(entityClass)) {
            if (annotationScanner.hasAnnotation(new PropertyReference(propertyName, entityClass), Id.class)) {
                identifierPropertyName = propertyName;
                break;
            }
        }
        return hasText(identifierPropertyName, "Could not find an identifier column.");
    }

    private Class<?> getTargetEntityClass(PropertyReference propertyReference) {
        ManyToOne manyToOne = annotationScanner.findAnnotation(propertyReference, ManyToOne.class);
        if (manyToOne.targetEntity() != void.class) {
            return manyToOne.targetEntity();
        } else {
            return getFieldType(propertyReference.getBeanClass(), propertyReference.getName());
        }
    }

    private String readColumnName(PropertyReference propertyReference) {
        Column column = annotationScanner.findAnnotation(propertyReference, Column.class);
        if (column != null && isNotBlank(column.name())) {
            return namingStrategy.columnName(column.name());
        } else {
            return namingStrategy.propertyToColumnName(propertyReference.getName());
        }
    }

}
