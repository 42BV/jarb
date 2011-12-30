package org.jarbframework.populator.excel.mapping.importer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link EntityImporter}.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class DefaultEntityImporter implements EntityImporter {
    private ExcelImporter delegateImporter;

    public DefaultEntityImporter(EntityManagerFactory entityManagerFactory, ValueConversionService conversionService) {
        delegateImporter = new ExcelImporter(conversionService, entityManagerFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry load(Workbook workbook, MetaModel metamodel) {
        List<Definition> entities = new ArrayList<Definition>(metamodel.entities());
        try {
            return delegateImporter.parseExcelToRegistry(workbook, entities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
