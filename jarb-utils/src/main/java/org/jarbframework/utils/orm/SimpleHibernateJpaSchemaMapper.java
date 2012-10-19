package org.jarbframework.utils.orm;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.jarbframework.utils.bean.PropertyReference;

public class SimpleHibernateJpaSchemaMapper implements SchemaMapper {
    
    private final SessionFactory sessionFactory;
    
    public SimpleHibernateJpaSchemaMapper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SimpleHibernateJpaSchemaMapper(EntityManager entityManager) {
        this(((Session) entityManager.getDelegate()).getSessionFactory());
    }
    
    public SimpleHibernateJpaSchemaMapper(EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory.createEntityManager());
    }
    
    @Override
    public String tableNameOf(Class<?> beanClass) {
        return getClassMetadata(beanClass).getTableName();
    }

    private AbstractEntityPersister getClassMetadata(Class<?> beanClass) {
        return (AbstractEntityPersister) sessionFactory.getClassMetadata(beanClass);
    }
    
    @Override
    public ColumnReference columnOf(PropertyReference propertyReference) {
        AbstractEntityPersister classMetadata = getClassMetadata(propertyReference.getBeanClass());
        String[] columnNames = classMetadata.getPropertyColumnNames(propertyReference.getName());
        if(columnNames.length == 1) {
            return new ColumnReference(classMetadata.getTableName(), columnNames[0]);
        } else {
            throw new IllegalStateException("Expected one column name.");
        }
    }

    @Override
    public boolean isEmbeddable(Class<?> beanClass) {
        return findAnnotation(beanClass, Embeddable.class) != null;
    }

    @Override
    public boolean isEntity(Class<?> beanClass) {
        return findAnnotation(beanClass, Entity.class) != null;
    }

}
