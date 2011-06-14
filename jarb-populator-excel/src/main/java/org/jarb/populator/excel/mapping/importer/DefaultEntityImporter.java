package org.jarb.populator.excel.mapping.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.ExcelRowIntegration;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link EntityImporter}.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class DefaultEntityImporter implements EntityImporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry load(Workbook workbook, MetaModel metamodel) {
        List<ClassDefinition<?>> classDefinitions = new ArrayList<ClassDefinition<?>>(metamodel.getClassDefinitions());
        try {
            Map<ClassDefinition<?>, Map<Integer, ExcelRow>> objectMap = ExcelImporter.parseExcel(workbook, classDefinitions);
            return ExcelRowIntegration.toRegistry(objectMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
