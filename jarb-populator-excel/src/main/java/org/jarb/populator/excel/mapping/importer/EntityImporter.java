package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Converts the rows of a {@link Workbook} into entities.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface EntityImporter {

    /**
     * Convert the content of a workbook into entities.
     * @param workbook contains the content
     * @param metamodel describes what our content means
     * @return entities derived from the content and metamodel
     */
    EntityRegistry load(Workbook workbook, MetaModel metamodel);

}
