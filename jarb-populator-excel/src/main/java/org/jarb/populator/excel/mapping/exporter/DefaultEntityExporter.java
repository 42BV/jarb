package org.jarb.populator.excel.mapping.exporter;

import java.util.List;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link EntityExporter}.
 * <b>Note that this component does not work yet!</b>
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class DefaultEntityExporter implements EntityExporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Workbook export(EntityRegistry registry, MetaModel metamodel) {
        Workbook workbook = new Workbook();
        for (ClassDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
            List<?> entities = registry.getAll(classDefinition.getPersistentClass()).list();
            workbook.addSheet(entitiesToSheet(entities, classDefinition));
        }
        return workbook;
    }

    // TODO: Note that component is not yet finished
    private Sheet entitiesToSheet(List<?> entities, ClassDefinition<?> classDefinition) {
        return new Sheet(classDefinition.getTableName());
    }

}
