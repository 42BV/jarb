package org.jarb.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.persist.EntityWriter;
import org.jarb.populator.excel.entity.persist.JpaEntityWriter;
import org.jarb.populator.excel.entity.query.EntityReader;
import org.jarb.populator.excel.entity.query.JpaEntityReader;
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
 * Factory that is capable of building {@link ExcelTestData} instances.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class ExcelTestDataFactory {
    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    //
    // Facade construction
    //

    /**
     * Build a new {@link ExcelTestData} instance.
     * @return fresh instance of excel test data
     */
    public ExcelTestData build() {
        ExcelTestData etd = new ExcelTestData();
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

    /**
     * Build a new {@link ExcelTestData} instance.
     * @param entityManagerFactory entity manager factory instance
     * @return fresh instance of excel test data
     */
    public static ExcelTestData build(EntityManagerFactory entityManagerFactory) {
        ExcelTestDataFactory factory = new ExcelTestDataFactory();
        factory.setEntityManagerFactory(entityManagerFactory);
        return factory.build();
    }

    //
    // Subcomponent construction
    //

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
        return new DefaultEntityExporter();
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
