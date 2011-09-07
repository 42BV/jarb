package org.jarb.violation.resolver.database;

import static org.jarb.utils.Conditions.notNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;

/**
 * Hibernate entity manager based database resolver implementation.
 * 
 * @author Jeroen van Schagen
 * @since 14-05-2011
 */
public class HibernateJpaDatabaseTypeResolver implements DatabaseTypeResolver {
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Construct a new {@link HibernateJpaDatabaseTypeResolver}.
     * @param entityManagerFactory entity manager factory, cannot be {@code null}
     */
    public HibernateJpaDatabaseTypeResolver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = notNull(entityManagerFactory, "Entity manager factory cannot be null.");
    }

    /**
     * Construct a new {@link HibernateJpaDatabaseTypeResolver} based on an entity manager.
     * @param entityManager entity manager containing our dialect
     * @return new database resolver for that entity manager
     */
    public static HibernateJpaDatabaseTypeResolver forEntityManager(EntityManager entityManager) {
        return new HibernateJpaDatabaseTypeResolver(entityManager.getEntityManagerFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseType resolve() {
        String hibernateDialect = entityManagerFactory.getProperties().get(Environment.DIALECT).toString();
        return HibernateDialectDatabaseTypeResolver.forName(hibernateDialect).resolve();
    }

}
