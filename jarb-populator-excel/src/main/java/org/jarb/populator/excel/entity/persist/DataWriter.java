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
class DataWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataWriter.class);

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
        EntityTransaction entityTransaction = entityManager.getTransaction();
        
        List<Object> entities = new ArrayList<Object>(createInstanceSet(registry));
        Collections.sort(entities, new ObjectClassInstantationComparator());
        
        entityTransaction.begin();
        for (Object entity : entities) {
            Set<Object> cascadedObjectsInThisInteration = new HashSet<Object>();
            cascadedObjectsInThisInteration.add(entity);
            entity = ReferentialPreparement.prepareEntityReferences(entity, entityManager, cascadedObjectsInThisInteration);
            Object identifier = JpaUtils.getIdentifier(entity, entityManagerFactory);
            LOGGER.info("Persisting entity '{}' #{}.", new Object[] { entity.getClass(), identifier });
            if (identifier == null || entityManager.find(entity.getClass(), identifier) == null) {
                entityManager.merge(entity);
            }
        }
        entityTransaction.commit();
    }
    
    private static Set<Object> createInstanceSet(EntityRegistry registry) {
        Set<Object> instanceSet = new HashSet<Object>();
        for(EntityTable<?> entities : registry) {
            for(Object entity : entities) {
                instanceSet.add(entity);
            }
        }
        return instanceSet;
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
