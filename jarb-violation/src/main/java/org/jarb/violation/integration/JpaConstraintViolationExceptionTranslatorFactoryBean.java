package org.jarb.violation.integration;

import javax.persistence.EntityManagerFactory;

import org.jarb.violation.resolver.database.HibernateJpaDatabaseResolver;

/**
 * Constructs a Hibernate JPA specific exception translator, requiring only an
 * entity manager factory reference. Using this factory bean should make the
 * initialization of exception translators significantly easier.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class JpaConstraintViolationExceptionTranslatorFactoryBean extends ConstraintViolationExceptionTranslatorFactoryBean {

    /**
     * Configures a {@link HibernateJpaDatabaseResolver} based on our entity manager factory.
     * @param entityManagerFactory the entity manager factory throwing exceptions
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        HibernateJpaDatabaseResolver databaseResolver = new HibernateJpaDatabaseResolver();
        databaseResolver.setEntityManagerFactory(entityManagerFactory);
        setDatabaseResolver(databaseResolver);
    }

}
