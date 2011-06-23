package org.jarb.populator.excel.entity.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.util.JpaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepares data for writing to a database and also saves the prepared data.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public final class DataWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataWriter.class);

    /**
     * Private constructor.
     */
    private DataWriter() {
    }
    
    public static Set<Object> createInstanceSet(EntityRegistry registry) {
        Set<Object> instanceSet = new HashSet<Object>();
        for(EntityTable<?> entities : registry) {
            for(Object entity : entities) {
                instanceSet.add(entity);
            }
        }
        return instanceSet;
    }

    /**
     * Saves the instances in the instance set to the database.
     * @param saveableInstances Instance set ready to save to the database
     * @param entityManagerFactory From ApplicationContext file, gives us the information we need to persist such as database
     * connectivity information.
     */
    public static void saveEntity(Set<Object> saveableInstances, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = JpaUtils.createEntityManager(entityManagerFactory);
        EntityTransaction entityTransaction = entityManager.getTransaction();

        Set<Object> cascadedObjectsInThisInteration = new HashSet<Object>();
        List<Object> savableInstancesList = new ArrayList<Object>(saveableInstances);
        Collections.sort(savableInstancesList, new ObjectClassInstantationComparator());
        entityTransaction.begin();

        for (Object saveableInstance : savableInstancesList) {
            cascadedObjectsInThisInteration.clear();
            cascadedObjectsInThisInteration.add(saveableInstance);
            LOGGER.info("Persisting Excelrow of class: " + saveableInstance.getClass());
            saveableInstance = ReferentialPreparement.prepareEntityReferences(saveableInstance, entityManager, cascadedObjectsInThisInteration);

            //Simply checking if the identifier is null is not good enough, since the ID could be preloaded. We must check the persistence context.
            Object identifier = JpaUtils.getIdentifier(saveableInstance, entityManagerFactory);
            if (identifier == null) {
                entityManager.merge(saveableInstance);
            } else if (entityManager.find(saveableInstance.getClass(), identifier) == null) {
                entityManager.merge(saveableInstance);
            }
        }
        entityTransaction.commit();
    }
    
    // Compares two objects based on class name
    private static class ObjectClassInstantationComparator implements Comparator<Object> {
        /** {@inheritDoc} */
        @Override
        public int compare(Object left, Object right) {
            return left.getClass().getName().compareTo(right.getClass().getName());
        }
    }
    
}
