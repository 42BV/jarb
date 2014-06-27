package org.jarbframework.utils.orm.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

public class HibernateUtils {

    /**
     * Retrieve the {@link DataSource} linked to that entity manager factory.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the data source
     */
    public static DataSource getDataSource(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = getSessionFactory(entityManagerFactory);
        return SessionFactoryUtils.getDataSource(sessionFactory);
    }
    
    @SuppressWarnings("deprecation")
    private static SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
        return ((org.hibernate.ejb.HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
    }

}
