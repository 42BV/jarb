package org.jarbframework.utils.orm.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

public class HibernateUtils {

    private static SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
        return ((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
    }

    public static DataSource getDataSource(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = getSessionFactory(entityManagerFactory);
        return SessionFactoryUtils.getDataSource(sessionFactory);
    }
    
}
