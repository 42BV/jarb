package org.jarbframework.populator.excel.entity.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepares data for writing to a database and also saves the prepared data.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public final class DataWriter {
    // TODO: Use JpaTemplate

    private static final Logger logger = LoggerFactory.getLogger(DataWriter.class);

    /**
     * Private constructor.
     */
    private DataWriter() {
    }

    /**
     * Saves the instances in the instance set to the database.
     * @param saveableInstances Instance set ready to save to the database
     * @param entityManagerFactory From ApplicationContext file, gives us the information we need to persist such as database
     * connectivity information.
     */
    public static void saveEntity(EntityRegistry registry, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = JpaUtils.createEntityManager(entityManagerFactory);
        EntityTransaction transaction = entityManager.getTransaction();

        List<Object> entities = new ArrayList<Object>(registry.all());
        Collections.sort(entities, new ObjectClassInstantationComparator());

        transaction.begin();
        try {
            for (Object entity : entities) {
                entity = new ReferentialPreparement(entityManager).prepareEntityReferences(entity);
                Object identifier = JpaUtils.getIdentifier(entity, entityManagerFactory);
                logger.debug("Persisting entity '{}'...", entity);
                if (identifier == null || entityManager.find(entity.getClass(), identifier) == null) {
                    entityManager.merge(entity);
                }
            }
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private static class ObjectClassInstantationComparator implements Comparator<Object> {

        @Override
        public int compare(Object left, Object right) {
            return left.getClass().getName().compareTo(right.getClass().getName());
        }

    }

}
