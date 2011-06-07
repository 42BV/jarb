package org.jarb.populator.excel.entity.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to retrieve data from the Database.
 * @author Sander Benschop
 *
 */
public final class DataReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataReader.class);

    /** Private constructor. */
    private DataReader() {
    }

    /**
     * Returns a list of records from the Database for a specified Entity.
     * @param entityManagerFactory EntityManagerFactory with database settings in it
     * @param entity Entity to retrieve from the database
     * @return List of database records
     * @throws ClassNotFoundException Thrown if class cannot be found
     */
    public static List<?> getTableFromDatabase(EntityManagerFactory entityManagerFactory, EntityType<?> entity) throws ClassNotFoundException {
        EntityManager entityManager = entityManagerFactory.createEntityManager(entityManagerFactory.getProperties());
        Class<?> foundClass = Class.forName(entity.getName());
        List<?> result = entityManager.createQuery("from " + foundClass.getName()).getResultList();
        LOGGER.info("Found {} entities for class {}", new Object[] { result.size(), entity.getName() });
        return result;
    }

}
