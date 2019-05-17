package nl._42.jarb.utils.orm.hibernate;

import nl._42.jarb.utils.bean.PropertyReference;
import nl._42.jarb.utils.orm.ColumnReference;
import nl._42.jarb.utils.orm.SchemaMapper;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.type.ComponentType;
import org.hibernate.type.MapType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.hibernate.persister.walking.spi.AssociationAttributeDefinition.AssociationNature.COLLECTION;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class HibernateJpaSchemaMapper implements SchemaMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final SessionFactory sessionFactory;
    
    public HibernateJpaSchemaMapper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String getTableName(Class<?> beanClass) {
        String tableName = null;

        AbstractEntityPersister metadata = safelyFindClassMetadata(beanClass);
        if (metadata != null) {
            tableName = metadata.getTableName();
        }

        return tableName;
    }

    private AbstractEntityPersister safelyFindClassMetadata(Class<?> beanClass) {
        AbstractEntityPersister metadata = null;

        try {
            metadata = (AbstractEntityPersister) sessionFactory.getClassMetadata(beanClass);
        } catch (HibernateException e) {
            logger.debug("Could not retrieve class metadata.", e);
        }

        return metadata;
    }
    
    @Override
    public ColumnReference getColumnReference(PropertyReference property) {
        ColumnReference column = null;

        try {
            AbstractEntityPersister classMetadata = safelyFindClassMetadata(property.getBeanClass());
            if (classMetadata != null) {
                column = getColumnName(property, classMetadata);
            }
        } catch (MappingException e) {
            logger.debug("Could not map property.", e);
        }

        return column;
    }

    private ColumnReference getColumnName(PropertyReference property, AbstractEntityPersister metadata) {
        NonIdentifierAttribute attribute = getAttribute(metadata, property.getBase());

        if (isCollection(attribute)) {
            return getCollectionColumnName(property.getSimpleName(), (AssociationAttributeDefinition) attribute);
        } else {
            return getPropertyColumnName(property.getPropertyName(), metadata);
        }
    }

    private boolean isCollection(NonIdentifierAttribute attribute) {
        boolean result = false;
        if (attribute instanceof AssociationAttributeDefinition) {
            AssociationAttributeDefinition association = (AssociationAttributeDefinition) attribute;
            if (Objects.equals(association.getAssociationNature(), COLLECTION)) {
                result = association.toCollectionDefinition().getCollectionPersister() instanceof BasicCollectionPersister;
            }
        }
        return result;
    }

    private ColumnReference getCollectionColumnName(String propertyName, AssociationAttributeDefinition attribute) {
        CollectionDefinition definition = attribute.toCollectionDefinition();
        BasicCollectionPersister persist = (BasicCollectionPersister) definition.getCollectionPersister();

        Type type;
        String[] columnNames;

        if (definition.getCollectionType() instanceof MapType && Objects.equals(propertyName, attribute.getName())) {
            type = persist.getIndexType();
            columnNames = persist.getIndexColumnNames();
        } else {
            type = persist.getElementType();
            columnNames = persist.getElementColumnNames();
        }

        String tableName = persist.getTableName();
        String columnName = getColumnName(propertyName, type, columnNames);

        return new ColumnReference(tableName, columnName);
    }

    private String getColumnName(String propertyName, Type type, String[] columnNames) {
        if (type instanceof ComponentType) {
            int index = ((ComponentType) type).getPropertyIndex(propertyName);
            return columnNames[index];
        } else {
            if (columnNames.length != 1) {
                throw new MappingException("Expected exactly one column name");
            }
            return columnNames[0];
        }
    }

    private NonIdentifierAttribute getAttribute(AbstractEntityPersister metadata, String name) {
        NonIdentifierAttribute[] properties = metadata.getEntityMetamodel().getProperties();

        return Stream.of(properties).filter(attribute ->
            attribute.getName().equals(name)
        ).findFirst().orElse(null);
    }

    private ColumnReference getPropertyColumnName(String propertyName, AbstractEntityPersister metadata) {
        String propertyTableName = metadata.getPropertyTableName(propertyName);

        String tableName = Objects.toString(propertyTableName, metadata.getTableName());
        String[] columnNames = metadata.getPropertyColumnNames(propertyName);

        if (columnNames.length == 1) {
            return new ColumnReference(tableName, columnNames[0]);
        } else {
            throw new MappingException(
                    format("Property '%s' is mapped to multiple columns.", propertyName)
            );
        }
    }

    @Override
    public boolean isEmbeddable(Class<?> beanClass) {
        return findAnnotation(beanClass, Embeddable.class) != null;
    }

}

