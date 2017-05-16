package org.jarbframework.utils.orm.hibernate;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate JPA implementation of {@link SchemaMapper}.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class HibernateJpaSchemaMapper implements SchemaMapper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /** Provides access to the class mapping meta-data. **/
    private final SessionFactory sessionFactory;
    
    public HibernateJpaSchemaMapper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public HibernateJpaSchemaMapper(EntityManager entityManager) {
        this(((Session) entityManager.getDelegate()).getSessionFactory());
    }
    
    public HibernateJpaSchemaMapper(EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory.createEntityManager());
    }
    
    @Override
    public String getTableName(Class<?> beanClass) {
        String tableName = null;
        AbstractEntityPersister classMetadata = safelyFindClassMetadata(beanClass);
        if(classMetadata != null) {
            tableName = classMetadata.getTableName();
        }
        return tableName;
    }

    private AbstractEntityPersister safelyFindClassMetadata(Class<?> beanClass) {
        AbstractEntityPersister classMetadata = null;
        try {
            classMetadata = (AbstractEntityPersister) sessionFactory.getClassMetadata(beanClass);   
        } catch(HibernateException e) {
            logger.debug("Could not retrieve class metadata.", e);
        }
        return classMetadata;
    }
    
    @Override
    public ColumnReference getColumnReference(PropertyReference propertyReference) {
        ColumnReference columnReference = null;
        try {
            AbstractEntityPersister classMetadata = safelyFindClassMetadata(propertyReference.getBeanClass());
            if (classMetadata != null) {
                String[] columnNames = classMetadata.getPropertyColumnNames(propertyReference.getPropertyName());
                if(columnNames.length == 1) {
                    columnReference = new ColumnReference(classMetadata.getTableName(), columnNames[0]);
                } else {
                    throw new IllegalStateException("Property '" + propertyReference + "' is mapped to multiple columns.");
                }
            }
        } catch(MappingException e) {
            logger.debug("Could not map property.", e);
        }
        return columnReference;
    }

    @Override
    public boolean isEmbeddable(Class<?> beanClass) {
        return findAnnotation(beanClass, Embeddable.class) != null;
    }

}
