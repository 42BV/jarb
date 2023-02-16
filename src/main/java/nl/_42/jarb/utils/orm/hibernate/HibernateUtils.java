package nl._42.jarb.utils.orm.hibernate;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;

import javax.sql.DataSource;

public class HibernateUtils {

    /**
     * Retrieve the {@link DataSource} linked to that entity manager factory.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the data source
     */
    public static DataSource getDataSource(EntityManagerFactory entityManagerFactory) {
        SessionFactoryImpl factory = getSessionFactory(entityManagerFactory);
        return factory.getServiceRegistry().getService(ConnectionProvider.class).unwrap(DataSource.class);
    }

    public static SessionFactoryImpl getSessionFactory(EntityManagerFactory entityManagerFactory) {
        Object unwrapped = entityManagerFactory.unwrap(null);
        if (unwrapped instanceof SessionFactoryImpl sessionFactory) {
            return sessionFactory;
        } else {
            throw new IllegalStateException("Could not extract data source from entity manager factory.");
        }
    }

}
