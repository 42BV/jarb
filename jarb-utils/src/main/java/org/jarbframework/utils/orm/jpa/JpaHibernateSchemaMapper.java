/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.utils.orm.jpa;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarbframework.utils.Conditions.hasText;
import static org.jarbframework.utils.Conditions.instanceOf;
import static org.jarbframework.utils.Conditions.notNull;
import static org.jarbframework.utils.bean.BeanProperties.getDeclaringClass;
import static org.jarbframework.utils.bean.BeanProperties.getPropertyNames;
import static org.jarbframework.utils.bean.BeanProperties.getPropertyType;
import static org.jarbframework.utils.orm.jpa.JpaMetaModelUtils.assertIsEntity;
import static org.jarbframework.utils.orm.jpa.JpaMetaModelUtils.findRootEntityClass;
import static org.springframework.beans.BeanUtils.instantiateClass;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jarbframework.utils.bean.BeanAnnotationScanner;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;

// TODO: Provide support for @EmbeddedId

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class JpaHibernateSchemaMapper implements SchemaMapper {
    private static final String NAMING_STRATEGY_PROP_KEY = "hibernate.ejb.naming_strategy";

    /** Retrieves the annotations of a bean class. */
    private final BeanAnnotationScanner annotationScanner = BeanAnnotationScanner.fieldOrGetter();
    
    /** Naming strategy used to determine the eventual mapping. */
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

    //
    // Table
    //

    @Override
    public String table(Class<?> entityClass) {
        assertIsEntity(entityClass);

        Class<?> tableClass = determineTableClass(entityClass);
        return readTableName(tableClass);
    }

    private Class<?> determineTableClass(Class<?> entityClass) {
        Class<?> tableClass = entityClass;
        Class<?> rootEntityClass = findRootEntityClass(entityClass);
        Inheritance inheritance = rootEntityClass.getAnnotation(Inheritance.class);
        if (inheritance != null && inheritance.strategy() == InheritanceType.SINGLE_TABLE) {
            tableClass = rootEntityClass;
        }
        return tableClass;
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

    //
    // Column
    //

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

    private boolean hasColumn(PropertyReference propertyReference) {
        return !(annotationScanner.hasAnnotation(propertyReference, Transient.class) || isCollectionReference(propertyReference));
    }

    private boolean isCollectionReference(PropertyReference propertyReference) {
        return
            annotationScanner.hasAnnotation(propertyReference, OneToMany.class) ||
            annotationScanner.hasAnnotation(propertyReference, ManyToMany.class) ||
            annotationScanner.hasAnnotation(propertyReference, ElementCollection.class) ;
    }

    private String tableForProperty(PropertyReference propertyReference) {
        Class<?> tableClass = propertyReference.getBeanClass();
        Class<?> rootEntityClass = findRootEntityClass(propertyReference.getBeanClass());
        Inheritance inheritance = rootEntityClass.getAnnotation(Inheritance.class);
        if (inheritance != null) {
            switch (inheritance.strategy()) {
            case SINGLE_TABLE:
                tableClass = rootEntityClass;
                break;
            case JOINED:
                tableClass = getDeclaringClass(propertyReference);
                break;
            }
        }
        return readTableName(tableClass);
    }

    private String columnName(PropertyReference propertyReference) {
        String columnName;
        if (annotationScanner.hasAnnotation(propertyReference, OneToOne.class)) {
            columnName = readOneToOne(propertyReference);
        } else if (annotationScanner.hasAnnotation(propertyReference, ManyToOne.class)) {
            columnName = readManyToOne(propertyReference);
        } else {
            columnName = readColumnName(propertyReference);
        }
        return columnName;
    }

    private String readOneToOne(PropertyReference propertyReference) {
        OneToOne oneToOne = annotationScanner.findAnnotation(propertyReference, OneToOne.class);
        Class<?> referencedClass = oneToOne.targetEntity() != void.class ? oneToOne.targetEntity() : getPropertyType(propertyReference);
        return readReferenceColumnName(propertyReference, referencedClass);
    }

    private String readManyToOne(PropertyReference propertyReference) {
        ManyToOne manyToOne = annotationScanner.findAnnotation(propertyReference, ManyToOne.class);
        Class<?> referencedClass = manyToOne.targetEntity() != void.class ? manyToOne.targetEntity() : getPropertyType(propertyReference);
        return readReferenceColumnName(propertyReference, referencedClass);
    }

    private String readReferenceColumnName(PropertyReference propertyReference, Class<?> referencedClass) {
        JoinColumn joinColumn = annotationScanner.findAnnotation(propertyReference, JoinColumn.class);
        if (joinColumn != null && isNotBlank(joinColumn.name())) {
            return namingStrategy.columnName(joinColumn.name());
        } else {
            String referencedProperty = getIdentifierPropertyName(referencedClass);
            ColumnReference referencedColumn = column(new PropertyReference(referencedClass, referencedProperty));
            return namingStrategy.foreignKeyColumnName(propertyReference.getName(), referencedClass.getName(), referencedColumn.getTableName(), referencedColumn.getColumnName());
        }
    }

    private String getIdentifierPropertyName(Class<?> entityClass) {
        String identifierProperty = null;
        for (String propertyName : getPropertyNames(entityClass)) {
            if (annotationScanner.hasAnnotation(new PropertyReference(entityClass, propertyName), Id.class)) {
                identifierProperty = propertyName;
                break;
            }
        }
        return hasText(identifierProperty, "Could not find an identifier column for '" + entityClass + "'.");
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
