/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarb.utils.BeanAnnotationUtils.getAnnotation;
import static org.jarb.utils.BeanAnnotationUtils.hasAnnotation;
import static org.jarb.utils.ReflectionUtils.getFieldNames;
import static org.jarb.utils.ReflectionUtils.getFieldType;
import static org.springframework.beans.BeanUtils.instantiateClass;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

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
        notNull(namingStrategy, "Naming strategy property is required");
        this.namingStrategy = namingStrategy;
    }

    public JpaHibernateSchemaMapper(EntityManagerFactory entityManagerFactory) {
        Object namingStrategy = entityManagerFactory.getProperties().get(NAMING_STRATEGY_KEY);
        if (namingStrategy == null) {
            this.namingStrategy = new DefaultNamingStrategy();
        } else {
            isInstanceOf(Class.class, namingStrategy, "Naming strategy needs to be a class");
            this.namingStrategy = instantiateClass((Class<? extends NamingStrategy>) namingStrategy);
        }
    }

    @Override
    public String table(Class<?> beanClass) {
        assertIsEntity(beanClass);

        Table table = beanClass.getAnnotation(Table.class);
        if (table != null && isNotBlank(table.name())) {
            return namingStrategy.tableName(table.name());
        } else {
            return extractEntityName(beanClass);
        }
    }

    private void assertIsEntity(Class<?> beanClass) {
        if (beanClass.getAnnotation(Entity.class) == null) {
            throw new NotAnEntityException("Bean class '" + beanClass.getName() + "' is not an @Entity.");
        }
    }

    private String extractEntityName(Class<?> entityClass) {
        String entityName = null;
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (isNotBlank(entity.name())) {
            entityName = entity.name();
        } else {
            entityName = entityClass.getSimpleName();
        }
        return namingStrategy.classToTableName(entityName);
    }

    @Override
    public String column(Class<?> beanClass, String propertyName) {
        assertIsEntity(beanClass);

        String columnName = null;
        if (hasAnnotation(beanClass, propertyName, ManyToOne.class)) {
            columnName = extractReferenceColumnName(beanClass, propertyName);
        } else if (!isCollectionReferenceColumn(beanClass, propertyName)) {
            columnName = extractColumnName(beanClass, propertyName);
        }
        return columnName;
    }

    private boolean isCollectionReferenceColumn(Class<?> beanClass, String propertyName) {
        return hasAnnotation(beanClass, propertyName, OneToMany.class) || hasAnnotation(beanClass, propertyName, ManyToMany.class);
    }

    private String extractReferenceColumnName(Class<?> beanClass, String propertyName) {
        if (hasJoinColumn(beanClass, propertyName)) {
            JoinColumn joinColumn = getAnnotation(beanClass, propertyName, JoinColumn.class);
            return namingStrategy.columnName(joinColumn.name());
        } else {
            Class<?> referencedEntity = getReferencedEntity(beanClass, propertyName);
            String identifierPropertyName = getIdentifierPropertyName(beanClass);
            String referenceColumnName = column(beanClass, identifierPropertyName);
            return namingStrategy.foreignKeyColumnName(propertyName, referencedEntity.getName(), table(referencedEntity), referenceColumnName);
        }
    }

    private Class<?> getReferencedEntity(Class<?> beanClass, String propertyName) {
        ManyToOne manyToOne = getAnnotation(beanClass, propertyName, ManyToOne.class);
        if (manyToOne.targetEntity() != void.class) {
            return manyToOne.targetEntity();
        } else {
            return getFieldType(beanClass, propertyName);
        }
    }

    private String getIdentifierPropertyName(Class<?> beanClass) {
        for (String fieldName : getFieldNames(beanClass)) {
            if (hasAnnotation(beanClass, fieldName, Id.class)) {
                return fieldName;
            }
        }
        throw new IllegalArgumentException("Bean class '" + beanClass + "' has no @Id property.");
    }

    private boolean hasJoinColumn(Class<?> beanClass, String propertyName) {
        JoinColumn joinColumn = getAnnotation(beanClass, propertyName, JoinColumn.class);
        return joinColumn != null && isNotBlank(joinColumn.name());
    }

    private String extractColumnName(Class<?> beanClass, String propertyName) {
        Column column = getAnnotation(beanClass, propertyName, Column.class);
        if (column != null && isNotBlank(column.name())) {
            return namingStrategy.columnName(column.name());
        } else {
            return namingStrategy.propertyToColumnName(propertyName);
        }
    }

}
