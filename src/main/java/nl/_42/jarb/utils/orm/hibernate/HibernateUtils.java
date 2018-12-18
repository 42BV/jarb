package nl._42.jarb.utils.orm.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class HibernateUtils {

    /**
     * Retrieve the {@link DataSource} linked to that entity manager factory.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the data source
     */
    public static DataSource getDataSource(EntityManagerFactory entityManagerFactory) {
        try {
            SessionFactoryImplementor sessionFactory = ((org.hibernate.jpa.HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
            return sessionFactory.getServiceRegistry().getService(ConnectionProvider.class).unwrap(DataSource.class);
        } catch (RuntimeException rte) {
            throw new IllegalStateException("Could not extract data source from entity manager factory.", rte);
        }
    }

}
