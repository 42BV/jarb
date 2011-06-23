package org.jarb.populator.excel.mapping.excelrow;

import java.util.Map;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.metamodel.ClassDefinition;

/**
 * Temporary integration utility, used to interface with old components.
 * Eventually, our components should implement the new interface, meaning
 * this utility would no longer be necesairy.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class ExcelRowIntegration {

    @SuppressWarnings("unchecked")
    public static EntityRegistry toRegistry(Map<ClassDefinition<?>, Map<Integer, ExcelRow>> entitiesMap) {
        EntityRegistry registry = new EntityRegistry();
        for (Map.Entry<ClassDefinition<?>, Map<Integer, ExcelRow>> entitiesEntry : entitiesMap.entrySet()) {
            @SuppressWarnings("rawtypes")
            final Class entityClass = entitiesEntry.getKey().getPersistentClass();
            EntityTable<Object> entities = new EntityTable<Object>(entityClass);
            for (Map.Entry<Integer, ExcelRow> excelRowEntry : entitiesEntry.getValue().entrySet()) {
                entities.add(excelRowEntry.getKey().longValue(), excelRowEntry.getValue().getCreatedInstance());
            }
            registry.addAll(entities);
        }
        return registry;
    }

}
