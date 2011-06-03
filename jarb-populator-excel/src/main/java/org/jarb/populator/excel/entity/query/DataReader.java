package org.jarb.populator.excel.entity.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

/**
 * Used to retrieve data from the Database.
 * @author Sander Benschop
 *
 */
public final class DataReader {

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
        System.out.printf("Found %d entities: %s for class %s \n", result.size(), result, entity.getName());
        return result;
    }

}
