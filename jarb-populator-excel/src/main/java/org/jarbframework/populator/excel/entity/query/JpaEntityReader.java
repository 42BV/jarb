package org.jarbframework.populator.excel.entity.query;

import static org.jarbframework.utils.orm.jpa.JpaMetaModelUtils.getRootEntities;
import static org.springframework.orm.jpa.EntityManagerFactoryUtils.getTransactionalEntityManager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.entity.EntityTable;
import org.jarbframework.populator.excel.util.JpaUtils;

/**
 * Java Persistence API (JPA) implementation of {@link EntityReader}.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class JpaEntityReader implements EntityReader {
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Construct a new {@link JpaEntityReader}.
     * @param entityManagerFactory entity manager factory used for retrieving data
     */
    public JpaEntityReader(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry readAll() {
        EntityRegistry registry = new EntityRegistry();
        EntityManager entityManager = getTransactionalEntityManager(entityManagerFactory);
        for (EntityType<?> entityType : getRootEntities(entityManagerFactory.getMetamodel())) {
            final Class<?> entityClass = entityType.getJavaType();
            registry.addAll(readFrom(entityClass, entityManager));
        }
        return registry;
    }

    private <T> EntityTable<T> readFrom(Class<T> entityClass, EntityManager entityManager) {
        EntityTable<T> entities = new EntityTable<T>(entityClass);
        for (T entity : findAll(entityClass, entityManager)) {
            entities.add(identifierOf(entity), entity);
        }
        return entities;
    }

    private <T> List<T> findAll(Class<T> entityClass, EntityManager entityManager) {
        return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }

    private Object identifierOf(Object entity) {
        return JpaUtils.getIdentifier(entity, entityManagerFactory);
    }

}
