package org.jarbframework.utils.orm.jpa;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.instanceOf;
import static org.jarbframework.utils.Asserts.notNull;
import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;
import static org.jarbframework.utils.bean.BeanProperties.getDeclaringClass;
import static org.jarbframework.utils.bean.BeanProperties.getPropertyNames;
import static org.jarbframework.utils.bean.BeanProperties.getPropertyType;
import static org.jarbframework.utils.orm.jpa.JpaMetaModelUtils.findRootEntityClass;
import static org.springframework.beans.BeanUtils.instantiateClass;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import org.jarbframework.utils.orm.NotAnEntityException;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class JpaHibernateSchemaMapper implements SchemaMapper {
    private static final String NAMING_STRATEGY_KEY = "hibernate.ejb.naming_strategy";

    private final BeanAnnotationScanner annotationScanner = fieldOrGetter();
    private final NamingStrategy namingStrategy;

    public JpaHibernateSchemaMapper() {
        this(new DefaultNamingStrategy());
    }

    public JpaHibernateSchemaMapper(NamingStrategy namingStrategy) {
        this.namingStrategy = notNull(namingStrategy, "Naming strategy property is required.");
    }

    public static JpaHibernateSchemaMapper usingNamingStrategyOf(EntityManagerFactory entityManagerFactory) {
        Object namingStrategyProperty = entityManagerFactory.getProperties().get(NAMING_STRATEGY_KEY);
        if (namingStrategyProperty == null) {
            return new JpaHibernateSchemaMapper();
        } else {
            String namingStrategyClass = instanceOf(namingStrategyProperty, String.class, format("Property '%s' should be a String.", NAMING_STRATEGY_KEY));
            return new JpaHibernateSchemaMapper(instantiateStrategy(namingStrategyClass));
        }
    }

    private static NamingStrategy instantiateStrategy(String className) {
        Class<?> namingStrategyClass;
        try {
            namingStrategyClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Naming strategy '" + className + "' is not available in the classpath.", e);
        }
        return (NamingStrategy) instantiateClass(namingStrategyClass);
    }

    @Override
    public boolean isEntity(Class<?> clazz) {
        return JpaMetaModelUtils.isEntity(clazz);
    }

    @Override
    public boolean isEmbeddable(Class<?> clazz) {
        return JpaMetaModelUtils.isEmbeddable(clazz);
    }

    // Table mapping

    @Override
    public String tableNameOf(Class<?> entityClass) {
        String tableName = null;
        if (isEntity(entityClass)) {
            Class<?> tableClass = determineTableClass(entityClass);
            tableName = readTableName(tableClass);
        }
        return tableName;
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
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (hasTableName(tableAnnotation)) {
            return namingStrategy.tableName(tableAnnotation.name());
        } else {
            return readEntityName(entityClass);
        }
    }

    private boolean hasTableName(Table tableAnnotation) {
        return tableAnnotation != null && isNotBlank(tableAnnotation.name());
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

    // Column mapping

    @Override
    public ColumnReference columnOf(PropertyReference propertyReference) {
        if (isEntity(propertyReference.getBeanClass())) {
            return createColumnReferenceFromEntity(propertyReference);
        } else if (isEmbeddable(propertyReference.getBeanClass())) {
            return createColumnReferenceFromEmbeddable(propertyReference);
        } else {
            throw new NotAnEntityException("Class '" + propertyReference.getBeanClass() + "' could not be recognized as entity.");
        }
    }

    private ColumnReference createColumnReferenceFromEmbeddable(PropertyReference propertyReference) {
        ColumnReference columnReference = null;
        if (isMappedToColumn(propertyReference)) {
            String columnName = columnName(propertyReference);
            columnReference = new ColumnReference(columnName);
        }
        return columnReference;
    }

    private ColumnReference createColumnReferenceFromEntity(PropertyReference propertyReference) {
        ColumnReference columnReference = null;
        if (isMappedToColumn(propertyReference)) {
            String tableName = tableForProperty(propertyReference);
            String columnName = columnName(propertyReference);
            columnReference = new ColumnReference(tableName, columnName);
        }
        return columnReference;
    }

    private boolean isMappedToColumn(PropertyReference propertyReference) {
        boolean mappedToColumn = false;
        if (!isCollection(propertyReference) && !isInverseReferenceColumn(propertyReference)) {
            mappedToColumn = !annotationScanner.hasAnnotation(propertyReference, Transient.class);
        }
        return mappedToColumn;
    }

    private boolean isInverseReferenceColumn(PropertyReference propertyReference) {
        boolean inverseReferenceColumn = false;
        if (annotationScanner.hasAnnotation(propertyReference, OneToOne.class)) {
            inverseReferenceColumn = isNotBlank(annotationScanner.findAnnotation(propertyReference, OneToOne.class).mappedBy());
        }
        return inverseReferenceColumn;
    }

    private boolean isCollection(PropertyReference propertyReference) {
        return annotationScanner.hasAnnotation(propertyReference, OneToMany.class) || annotationScanner.hasAnnotation(propertyReference, ManyToMany.class)
                || annotationScanner.hasAnnotation(propertyReference, ElementCollection.class);
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
            default:
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
        if (propertyReference.isNestedProperty()) {
            columnName = includeAttributeOverrides(columnName, propertyReference);
        }
        return columnName;
    }

    private String includeAttributeOverrides(String columnName, PropertyReference propertyReference) {
        for (AttributeOverride attributeOverride : collectAttributeOverrides(propertyReference.getParent())) {
            if (propertyReference.getSimpleName().equals(attributeOverride.name())) {
                if (hasColumnName(attributeOverride.column())) {
                    columnName = namingStrategy.columnName(attributeOverride.column().name());
                }
                break;
            }
        }
        return columnName;
    }

    private AttributeOverride[] collectAttributeOverrides(PropertyReference propertyReference) {
        if (annotationScanner.hasAnnotation(propertyReference, AttributeOverrides.class)) {
            return annotationScanner.findAnnotation(propertyReference, AttributeOverrides.class).value();
        } else if (annotationScanner.hasAnnotation(propertyReference, AttributeOverride.class)) {
            return new AttributeOverride[] { annotationScanner.findAnnotation(propertyReference, AttributeOverride.class) };
        } else {
            return new AttributeOverride[0];
        }
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

    private String readReferenceColumnName(PropertyReference propertyReference, Class<?> referencingClass) {
        JoinColumn joinColumn = annotationScanner.findAnnotation(propertyReference, JoinColumn.class);
        if (joinColumn != null && isNotBlank(joinColumn.name())) {
            return namingStrategy.columnName(joinColumn.name());
        } else {
            String referencedPropertyName = getIdentifierPropertyName(referencingClass);
            ColumnReference referencedColumn = columnOf(new PropertyReference(referencingClass, referencedPropertyName));
            return namingStrategy.foreignKeyColumnName(propertyReference.getName(), referencingClass.getName(), referencedColumn.getTableName(),
                    referencedColumn.getColumnName());
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
        Column columnAnnotation = annotationScanner.findAnnotation(propertyReference, Column.class);
        if (hasColumnName(columnAnnotation)) {
            return namingStrategy.columnName(columnAnnotation.name());
        } else {
            return namingStrategy.propertyToColumnName(propertyReference.getName());
        }
    }

    private boolean hasColumnName(Column columnAnnotation) {
        return columnAnnotation != null && isNotBlank(columnAnnotation.name());
    }
}
