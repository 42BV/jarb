package org.jarb.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.persist.EntityWriter;
import org.jarb.populator.excel.entity.persist.JpaEntityWriter;
import org.jarb.populator.excel.entity.query.EntityReader;
import org.jarb.populator.excel.entity.query.JpaEntityReader;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.mapping.exporter.DefaultEntityExporter;
import org.jarb.populator.excel.mapping.exporter.EntityExporter;
import org.jarb.populator.excel.mapping.importer.DefaultEntityImporter;
import org.jarb.populator.excel.mapping.importer.EntityImporter;
import org.jarb.populator.excel.metamodel.generator.JpaMetaModelGenerator;
import org.jarb.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarb.populator.excel.workbook.reader.WorkbookParser;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarb.populator.excel.workbook.validator.DefaultWorkbookValidator;
import org.jarb.populator.excel.workbook.validator.WorkbookValidator;
import org.jarb.populator.excel.workbook.writer.WorkbookWriter;
import org.jarb.populator.excel.workbook.writer.PoiWorkbookWriter;

/**
 * Factory that is capable of building {@link ExcelDataManager} instances.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class ExcelDataManagerFactory {
    private EntityManagerFactory entityManagerFactory;
    private ValueConversionService valueConversionService;

    public ExcelDataManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        valueConversionService = new ValueConversionService();
    }
    
    public void setValueConversionService(ValueConversionService valueConversionService) {
        this.valueConversionService = valueConversionService;
    }

    /**
     * Build a new {@link ExcelDataManager} instance.
     * @return fresh instance of excel test data
     */
    public ExcelDataManager build() {
        ExcelDataManager etd = new ExcelDataManager();
        etd.setExcelParser(buildExcelParser());
        etd.setExcelWriter(buildExcelWriter());
        etd.setEntityImporter(buildEntityImporter());
        etd.setEntityExporter(buildEntityExporter());
        etd.setEntityReader(buildEntityReader());
        etd.setEntityWriter(buildEntityWriter());
        etd.setExcelValidator(buildExcelValidator());
        etd.setMetamodelGenerator(buildMetamodelGenerator());
        return etd;
    }

    public WorkbookParser buildExcelParser() {
        return new PoiWorkbookParser();
    }

    public WorkbookWriter buildExcelWriter() {
        return new PoiWorkbookWriter();
    }

    public EntityImporter buildEntityImporter() {
        return new DefaultEntityImporter(entityManagerFactory);
    }

    public EntityExporter buildEntityExporter() {
        return new DefaultEntityExporter(valueConversionService);
    }

    public EntityReader buildEntityReader() {
        return new JpaEntityReader(entityManagerFactory);
    }

    public EntityWriter buildEntityWriter() {
        return new JpaEntityWriter(entityManagerFactory);
    }

    public MetaModelGenerator buildMetamodelGenerator() {
        return new JpaMetaModelGenerator(entityManagerFactory);
    }

    public WorkbookValidator buildExcelValidator() {
        return new DefaultWorkbookValidator();
    }

}
