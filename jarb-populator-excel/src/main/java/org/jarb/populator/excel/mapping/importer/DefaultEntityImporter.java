package org.jarb.populator.excel.mapping.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.util.JpaUtils;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link EntityImporter}.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class DefaultEntityImporter implements EntityImporter {
    private final EntityManagerFactory entityManagerFactory;
    
    public DefaultEntityImporter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry load(Workbook workbook, MetaModel metamodel) {
        List<EntityDefinition<?>> classDefinitions = new ArrayList<EntityDefinition<?>>(metamodel.entities());
        try {
            Map<EntityDefinition<?>, Map<Object, ExcelRow>> objectMap = ExcelImporter.parseExcel(workbook, classDefinitions);
            return toRegistry(objectMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private EntityRegistry toRegistry(Map<EntityDefinition<?>, Map<Object, ExcelRow>> entitiesMap) {
        EntityRegistry registry = new EntityRegistry();
        for (Map.Entry<EntityDefinition<?>, Map<Object, ExcelRow>> entitiesEntry : entitiesMap.entrySet()) {
            @SuppressWarnings("rawtypes")
            final Class entityClass = entitiesEntry.getKey().getEntityClass();
            EntityTable<Object> entities = new EntityTable<Object>(entityClass);
            for (Map.Entry<Object, ExcelRow> excelRowEntry : entitiesEntry.getValue().entrySet()) {
                // Entity registry uses the entities persistent identifier, rather than its row identifier
                // Row identifier is only used in excel, entity identifier is used in registry and database
                Object entity = excelRowEntry.getValue().getCreatedInstance();
                Object identifier = JpaUtils.getIdentifier(entity, entityManagerFactory);
                if(identifier == null) {
                    // Whenever the identifier is null, because it has not yet been defined (generated value)
                    // use a random placeholder identifier. This identifier is only used to access the entity
                    // inside the generated entity registry. After persisting the registry, our entity identifier
                    // will be replaced with the actual database identifier.
                    identifier = UUID.randomUUID().toString();
                }
                entities.add(identifier,entity );
            }
            registry.addAll(entities);
        }
        return registry;
    }

}
