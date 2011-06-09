package org.jarb.violation.resolver.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;
import org.springframework.util.Assert;

/**
 * Hibernate entity manager based database resolver implementation.
 * 
 * @author Jeroen van Schagen
 * @since 14-05-2011
 */
public class HibernateJpaDatabaseResolver implements DatabaseResolver {
    private final EntityManagerFactory entityManagerFactory;
    
    /**
     * Construct a new {@link HibernateJpaDatabaseResolver}.
     * @param entityManagerFactory entity manager factory containing our dialect
     */
    public HibernateJpaDatabaseResolver(EntityManagerFactory entityManagerFactory) {
        Assert.notNull(entityManagerFactory, "Entity manager factory cannot be null");
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Construct a new {@link HibernateJpaDatabaseResolver} based on an entity manager.
     * @param entityManager entity manager containing our dialect
     * @return new database resolver for that entity manager
     */
    public static HibernateJpaDatabaseResolver forEntityManager(EntityManager entityManager) {
        return new HibernateJpaDatabaseResolver(entityManager.getEntityManagerFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Database resolve() {
        String hibernateDialect = entityManagerFactory.getProperties().get(Environment.DIALECT).toString();
        return HibernateDialectDatabaseResolver.resolveByDialect(hibernateDialect);
    }
}
