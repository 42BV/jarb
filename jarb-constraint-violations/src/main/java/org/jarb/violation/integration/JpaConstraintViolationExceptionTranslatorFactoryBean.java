package org.jarb.violation.integration;

import javax.persistence.EntityManagerFactory;

import org.jarb.violation.resolver.database.HibernateJpaDatabaseResolver;

/**
 * Constructs an exception translator that uses the hibernate dialect, inside our entity
 * manager factory, to resolve the database type. Whenever using this factory bean, only
 * an entity manager factory reference is required.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class JpaConstraintViolationExceptionTranslatorFactoryBean extends ConstraintViolationExceptionTranslatorFactoryBean {

    /**
     * Configures a default violation resolver that uses our hibernate dialect to resolve the database type.
     * @param entityManagerFactory the entity manager factory throwing exceptions
     * @see #setViolationResolver(org.jarb.violation.resolver.ConstraintViolationResolver)
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        setDatabaseResolver(new HibernateJpaDatabaseResolver(entityManagerFactory));
    }

}
