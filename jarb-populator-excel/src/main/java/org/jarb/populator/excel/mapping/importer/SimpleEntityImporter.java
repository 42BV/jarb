package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup to new entity importer implementation.
 * @author Jeroen van Schagen
 * @since 14-06-2011
 */
public class SimpleEntityImporter implements EntityImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEntityImporter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry load(Workbook workbook, MetaModel metamodel) {
        EntityRegistry registry = new EntityRegistry();
        for(ClassDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
            LOGGER.info("Importing {}...", classDefinition.getTableName());
            registry.addAll(loadEntities(workbook, classDefinition));
        }
        return registry;
    }
    
    private <T> EntityTable<T> loadEntities(Workbook workbook, ClassDefinition<T> classDefinition) {
        EntityTable<T> entities = new EntityTable<T>(classDefinition.getPersistentClass());
        Sheet sheet = workbook.getSheet(classDefinition.getTableName());
        for (Row row : sheet.getRows()) {
            // TODO: Do stuff
        }
        return entities;
    }

}
