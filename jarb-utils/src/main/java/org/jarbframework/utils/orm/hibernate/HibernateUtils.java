package org.jarbframework.utils.orm.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;

public class HibernateUtils {

    /**
     * Retrieve the {@link DataSource} linked to that entity manager factory.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the data source
     */
    public static DataSource getDataSource(EntityManagerFactory entityManagerFactory) {
        try {
            SessionFactory sessionFactory = ((org.hibernate.jpa.HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
            return ((org.hibernate.engine.spi.SessionFactoryImplementor) sessionFactory).getConnectionProvider().unwrap(DataSource.class);
        } catch (RuntimeException rte) {
            throw new IllegalStateException("Could not extract data source from entity manager factory.", rte);
        }
    }

}
