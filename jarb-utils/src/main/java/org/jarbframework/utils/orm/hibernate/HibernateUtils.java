package org.jarbframework.utils.orm.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;

public class HibernateUtils {

    public static Session getSession(EntityManager entityManager) {
        return (Session) entityManager.getDelegate();
    }
    
    public static Session getSession(EntityManagerFactory entityManagerFactory) {
        return getSession(entityManagerFactory.createEntityManager());
    }
    
}
