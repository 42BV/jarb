package nl._42.jarb.utils.orm.hibernate;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityManagerFactory;
import nl._42.jarb.utils.bean.PropertyReference;
import nl._42.jarb.utils.orm.ColumnReference;
import nl._42.jarb.utils.orm.SchemaMapper;
import org.hibernate.MappingException;
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.mapping.ManagedMappingType;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.metamodel.mapping.internal.EmbeddedAttributeMapping;
import org.hibernate.metamodel.mapping.internal.EmbeddedCollectionPart;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.metamodel.spi.RuntimeMetamodelsImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class HibernateJpaSchemaMapper implements SchemaMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RuntimeMetamodelsImplementor metamodels;

    public HibernateJpaSchemaMapper(EntityManagerFactory entityManagerFactory) {
        this.metamodels = HibernateUtils.getSessionFactory(entityManagerFactory).getRuntimeMetamodels();
    }

    @Override
    public ColumnReference getColumnReference(PropertyReference property) {
        ColumnReference column = null;

        try {
            EntityMappingType type = metamodels.getEntityMappingType(property.getBeanClass());
            if (type instanceof AbstractEntityPersister model) {
                column = getColumnName(model, property);
            }
        } catch (AssertionError | RuntimeException e) {
            logger.debug("Could not map property.", e);
        }

        return column;
    }

    private ColumnReference getColumnName(AbstractEntityPersister model, PropertyReference property) {
        if (!property.isNestedProperty()) {
            return getPropertyColumnName(model, property.getPropertyName());
        }

        AttributeMapping attribute = getAttribute(model, property);
        if (attribute.isPluralAttributeMapping()) {
            return getCollectionColumnName(model, property);
        } else {
            return getComponentColumnName(model, property);
        }
    }

    private ColumnReference getPropertyColumnName(AbstractEntityPersister model, String propertyName) {
        String propertyTableName = model.getPropertyTableName(propertyName);
        String tableName = Objects.toString(propertyTableName, model.getTableName());

        String[] columnNames = model.getPropertyColumnNames(propertyName);
        return new ColumnReference(tableName, columnNames[0]);
    }

    private AttributeMapping getAttribute(AbstractEntityPersister model, PropertyReference property) {
        var attribute = model.findAttributeMapping(property.getBase());

        if (attribute == null) {
            throw new MappingException(String.format("Could not find property %s", property.getBase()));
        }

        return attribute;
    }

    private ColumnReference getCollectionColumnName(AbstractEntityPersister model, PropertyReference property) {
        NavigableRole role = new NavigableRole(model.getNavigableRole(), property.getParent().getPropertyName()).appendContainer("{element}");
        EmbeddedCollectionPart embedded = (EmbeddedCollectionPart) metamodels.getEmbedded(role);
        AttributeMapping mapping = getAttributeMapping(embedded.getEmbeddableTypeDescriptor(), property.getSimpleName());
        return getColumnName(mapping);
    }

    private ColumnReference getComponentColumnName(AbstractEntityPersister model, PropertyReference property) {
        NavigableRole role = new NavigableRole(model.getNavigableRole(), property.getParent().getPropertyName());
        EmbeddedAttributeMapping embedded = (EmbeddedAttributeMapping) metamodels.getEmbedded(role);
        AttributeMapping mapping = getAttributeMapping(embedded.getMappedType(), property.getSimpleName());
        return getColumnName(mapping);
    }

    private AttributeMapping getAttributeMapping(ManagedMappingType type, String name) {
        var mapping = type.findAttributeMapping(name);
        if (mapping == null) {
            throw new MappingException(String.format("Could not find property %s", name));
        }
        return mapping;
    }

    private ColumnReference getColumnName(AttributeMapping mapping) {
        if (mapping instanceof BasicAttributeMapping basic) {
            return new ColumnReference(basic.getContainingTableExpression(), basic.getSelectionExpression());
        }
        return null;
    }

    @Override
    public boolean isEmbeddable(Class<?> beanClass) {
        return findAnnotation(beanClass, Embeddable.class) != null;
    }
}
