package org.jarb.populator.excel.entity.persist;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.EntityRegistry;

/**
 * Java Persistence API (JPA) implementation of {@link EntityWriter}.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class JpaEntityWriter implements EntityWriter {
    private final EntityManagerFactory entityManagerFactory;

    public JpaEntityWriter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry persist(EntityRegistry registry) {
        DataWriter.saveEntity(registry, entityManagerFactory);
        return registry;
    }

}
