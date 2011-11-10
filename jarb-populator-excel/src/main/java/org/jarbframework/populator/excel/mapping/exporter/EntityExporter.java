package org.jarbframework.populator.excel.mapping.exporter;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.workbook.Workbook;

/**
 * Converts entities into a {@link Workbook}.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface EntityExporter {

    /**
     * Convert a registry of entities into a workbook.
     * @param registry entities that should be converted into a workbook
     * @param metamodel describes how our entities should be mapped
     * @return workbook containing all entities
     */
    Workbook export(EntityRegistry registry, MetaModel metamodel);

}
