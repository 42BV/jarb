package org.jarb.populator.excel;

import java.io.InputStream;
import java.io.OutputStream;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.persist.EntityWriter;
import org.jarb.populator.excel.entity.query.EntityReader;
import org.jarb.populator.excel.mapping.exporter.EntityExporter;
import org.jarb.populator.excel.mapping.importer.EntityImporter;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.ExcelParser;
import org.jarb.populator.excel.workbook.validator.ExcelValidator;
import org.jarb.populator.excel.workbook.validator.ValidationResult;
import org.jarb.populator.excel.workbook.writer.ExcelWriter;

/**
 * Excel test data facade, provides all functionality.
 * @author Sander Benschop
 * @author Jeroen van Schagen
 */
public class ExcelDataManager {
    private ExcelParser excelParser;
    private ExcelWriter excelWriter;
    private EntityImporter entityImporter;
    private EntityExporter entityExporter;
    private EntityReader entityReader;
    private EntityWriter entityWriter;
    private ExcelValidator excelValidator;
    private MetaModelGenerator metamodelGenerator;

    /**
     * Read all entities from the specified excel resource and persist them in the database.
     * @param resource excel workbook resource
     */
    public void persistWorkbook(InputStream is) {
        EntityRegistry registry = loadWorkbook(is);
        entityWriter.persist(registry);
    }
    
    /**
     * Read all entities from the excel resource.
     * @param resource excel workbook resource
     * @return registry of all entities
     */
    public EntityRegistry loadWorkbook(InputStream is) {
        Workbook workbook = excelParser.parse(is);
        MetaModel metamodel = metamodelGenerator.generate();
        return entityImporter.load(workbook, metamodel);
    }
    
    /**
     * Verify some excel workbook against our current mappings.
     * @param resource excel workbook resource
     */
    public ValidationResult validateWorkbook(InputStream is) {
        Workbook workbook = excelParser.parse(is);
        MetaModel metamodel = metamodelGenerator.generate();
        return excelValidator.validate(workbook, metamodel);
    }

    /**
     * Create a new excel workbook based on the current mapping.
     * @param os output stream to the excel resource
     */
    public void createWorkbookTemplate(OutputStream os) {
        MetaModel metamodel = metamodelGenerator.generate();
        EntityRegistry emptyRegistry = new EntityRegistry();
        Workbook workbook = entityExporter.export(emptyRegistry, metamodel);
        excelWriter.write(workbook, os);
    }

    /**
     * Create a new excel workbook based on the current mapping and database data.
     * @param os output stream to the excel resource
     */
    public void createWorkbookWithDatabaseData(OutputStream os) {
        createWorkbookWithData(os, entityReader.fetchAll());
    }
    
    /**
     * Create a new excel workbook based on the current mapping and database data.
     * @param os output stream to the excel resource
     * @param registry the entities that should be included
     */
    public void createWorkbookWithData(OutputStream os, EntityRegistry registry) {
        MetaModel metamodel = metamodelGenerator.generate();
        Workbook workbook = entityExporter.export(registry, metamodel);
        excelWriter.write(workbook, os);
    }

    public void setExcelParser(ExcelParser excelParser) {
        this.excelParser = excelParser;
    }

    public void setExcelWriter(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }

    public void setEntityImporter(EntityImporter entityImporter) {
        this.entityImporter = entityImporter;
    }

    public void setEntityExporter(EntityExporter entityExporter) {
        this.entityExporter = entityExporter;
    }

    public void setEntityReader(EntityReader entityReader) {
        this.entityReader = entityReader;
    }

    public void setEntityWriter(EntityWriter entityWriter) {
        this.entityWriter = entityWriter;
    }

    public void setExcelValidator(ExcelValidator excelValidator) {
        this.excelValidator = excelValidator;
    }

    public void setMetamodelGenerator(MetaModelGenerator metaModelGenerator) {
        this.metamodelGenerator = metaModelGenerator;
    }

}
