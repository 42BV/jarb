package org.jarbframework.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.excel.entity.persist.EntityWriter;
import org.jarbframework.populator.excel.entity.persist.JpaEntityWriter;
import org.jarbframework.populator.excel.entity.query.EntityReader;
import org.jarbframework.populator.excel.entity.query.JpaEntityReader;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.mapping.exporter.DefaultEntityExporter;
import org.jarbframework.populator.excel.mapping.exporter.EntityExporter;
import org.jarbframework.populator.excel.mapping.importer.DefaultEntityImporter;
import org.jarbframework.populator.excel.mapping.importer.EntityImporter;
import org.jarbframework.populator.excel.metamodel.generator.JpaMetaModelGenerator;
import org.jarbframework.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.populator.excel.workbook.reader.WorkbookParser;
import org.jarbframework.populator.excel.workbook.validator.DefaultWorkbookValidator;
import org.jarbframework.populator.excel.workbook.validator.WorkbookValidator;
import org.jarbframework.populator.excel.workbook.writer.PoiWorkbookWriter;
import org.jarbframework.populator.excel.workbook.writer.WorkbookWriter;

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