package org.jarb.violation.resolver.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;
import org.jarb.violation.resolver.Database;

/**
 * Hibernate entity manager based database resolver implementation.
 * 
 * @author Jeroen van Schagen
 * @since 14-05-2011
 */
public class HibernateJpaDatabaseResolver implements DatabaseResolver {
    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * 
     * @param entityManager
     * @return
     */
    public static HibernateJpaDatabaseResolver forEntityManager(EntityManager entityManager) {
        HibernateJpaDatabaseResolver resolver = new HibernateJpaDatabaseResolver();
        resolver.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        return resolver;
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
