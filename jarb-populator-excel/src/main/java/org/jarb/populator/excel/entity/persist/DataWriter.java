package org.jarb.populator.excel.entity.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
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

    /**
     * Creates an instance set ready to be saved to the database with instances containing all Excel records.
     * @param objectModel Map with ClassDefinitions and their corresponding Excel records 
     * @return Instance set ready to save to the database
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException 
     */
    public static List<Object> createConnectionInstanceSet(Map<Class<?>, Map<Integer, ExcelRow>> objectModel) throws InstantiationException,
            IllegalAccessException, NoSuchFieldException {
        List<Object> instances = new ArrayList<Object>();

        for (Entry<Class<?>, Map<Integer, ExcelRow>> classRecord : objectModel.entrySet()) {
            LOGGER.info(classRecord.getKey().getName());
            for (Entry<Integer, ExcelRow> classValues : classRecord.getValue().entrySet()) {
                LOGGER.info("" + classValues.getKey());
                ExcelRow excelRow = classValues.getValue();
                // Relations are now made inside the importer, allowing clean conversion
                // ForeignRelationsMapper.makeForeignRelations(excelRow, objectModel);
                instances.add(excelRow.getCreatedInstance());
            }
        }
        return instances;
    }

    /**
     * Saves the instances in the instance set to the database.
     * @param saveableInstances Instance set ready to save to the database
     * @param entityManagerFactory From ApplicationContext file, gives us the information we need to persist such as database
     * connectivity information.
     */
    public static void saveEntity(List<Object> saveableInstances, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager(entityManagerFactory.getProperties());
        EntityTransaction entityTransaction = entityManager.getTransaction();

        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        Set<Object> cascadedObjectsInThisInteration = new HashSet<Object>();
        Collections.sort(saveableInstances, new ObjectClassInstantationComparator());
        entityTransaction.begin();

        for (Object saveableInstance : saveableInstances) {
            cascadedObjectsInThisInteration.clear();
            cascadedObjectsInThisInteration.add(saveableInstance);
            LOGGER.info("Persisting Excelrow of class: " + saveableInstance.getClass());
            saveableInstance = ReferentialPreparement.prepareEntityReferences(saveableInstance, entityManager, cascadedObjectsInThisInteration);

            //Simply checking if the identifier is null is not good enough, since the ID could be preloaded. We must check the persistence context.
            Object identifier = persistenceUnitUtil.getIdentifier(saveableInstance);
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
