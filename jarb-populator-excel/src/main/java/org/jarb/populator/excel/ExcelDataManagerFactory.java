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
import org.jarb.populator.excel.workbook.reader.ExcelParser;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.jarb.populator.excel.workbook.validator.DefaultExcelValidator;
import org.jarb.populator.excel.workbook.validator.ExcelValidator;
import org.jarb.populator.excel.workbook.writer.ExcelWriter;
import org.jarb.populator.excel.workbook.writer.PoiExcelWriter;

/**
 * Factory that is capable of building {@link ExcelDataManager} instances.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class ExcelDataManagerFactory {
    private final EntityManagerFactory entityManagerFactory;
    private ValueConversionService valueConversionService;

    public ExcelDataManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        valueConversionService = new ValueConversionService();
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
        // TODO: Remove entity manager factory reference from ETD
        etd.setEntityManagerFactory(entityManagerFactory);
        return etd;
    }

    public ExcelParser buildExcelParser() {
        return new PoiExcelParser();
    }

    public ExcelWriter buildExcelWriter() {
        return new PoiExcelWriter();
    }

    public EntityImporter buildEntityImporter() {
        return new DefaultEntityImporter();
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

    public ExcelValidator buildExcelValidator() {
        return new DefaultExcelValidator();
    }

}
