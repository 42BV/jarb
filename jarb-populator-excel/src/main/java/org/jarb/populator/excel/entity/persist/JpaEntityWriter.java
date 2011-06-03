package org.jarb.populator.excel.entity.persist;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.ExcelRowIntegration;

/**
 * Java Persistence API (JPA) implementation of {@link EntityWriter}.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class JpaEntityWriter implements EntityWriter {
    private final EntityManagerFactory entityManagerFactory;

    public JpaEntityWriter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void persist(EntityRegistry registry) {
        Map<Class<?>, Map<Integer, ExcelRow>> entitiesMap = ExcelRowIntegration.toMap(registry);
        try {
            List<Object> saveableInstances = DataWriter.createConnectionInstanceSet(entitiesMap);
            DataWriter.saveEntity(saveableInstances, entityManagerFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
