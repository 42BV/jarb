package org.jarb.populator.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.IOUtils;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.persist.EntityWriter;
import org.jarb.populator.excel.entity.query.EntityReader;
import org.jarb.populator.excel.mapping.exporter.EntityExporter;
import org.jarb.populator.excel.mapping.importer.EntityImporter;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.generator.FilledExcelFileGenerator;
import org.jarb.populator.excel.workbook.generator.NewExcelFileGenerator;
import org.jarb.populator.excel.workbook.reader.ExcelParser;
import org.jarb.populator.excel.workbook.validator.ExcelValidator;
import org.jarb.populator.excel.workbook.validator.ValidationResult;
import org.jarb.populator.excel.workbook.writer.ExcelWriter;

/**
 * Excel test data facade, provides all functionality.
 * @author Sander Benschop
 */
@SuppressWarnings("unused")
public class ExcelDataManager {
    private ExcelParser excelParser;
    private ExcelWriter excelWriter;
    private EntityImporter entityImporter;
    private EntityExporter entityExporter;
    private EntityReader entityReader;
    private EntityWriter entityWriter;
    private ExcelValidator excelValidator;
    private MetaModelGenerator metamodelGenerator;

    // TODO: Remove entity manager factory, and delegate all to the above components
    private EntityManagerFactory entityManagerFactory;

    /**
     * The main functionality of the component: read data from the specified Excel file and persist this.
     * @param is Stream of the Excel file which is to be persisted
     */
    public void persistWorkbook(InputStream is) {
        try {
            Workbook workbook = excelParser.parse(is);
            MetaModel metamodel = metamodelGenerator.generate();
            // TODO: Figure out why this worksheet definition has to be added
            ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(metamodel.getClassDefinitions(), workbook);
            EntityRegistry registry = entityImporter.load(workbook, metamodel);
            entityWriter.persist(registry);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * The main functionality of the component: read data from the specified Excel file and persist this.
     * @param excelFile The Excel file which is to be persisted
     */
    public void persistWorkbook(String excelFile) throws FileNotFoundException {
        persistWorkbook(new FileInputStream(excelFile));
    }

    /**
     * Sheet validator: tests the specified Excel file against the current mapping and checks for differences.
     * @param is stream to the excel file being tested
     */
    public ValidationResult validateWorkbook(InputStream is) {
        Workbook workbook = excelParser.parse(is);
        MetaModel metamodel = metamodelGenerator.generate();
        return excelValidator.validate(workbook, metamodel);
    }

    /**
     * Sheet validator: tests the specified Excel file against the current mapping and checks for differences.
     * @param excelFile path to the excel file being tested
     * @throws FileNotFoundException when the file cannot be found
     */
    public ValidationResult validateWorkbook(String excelFile) throws FileNotFoundException {
        return validateWorkbook(new FileInputStream(excelFile));
    }

    /**
     * New sheet generator: generates a new Excel file at the specified destination based on the current mapping.
     * @param os Stream to the Excel file that is to be created
     */
    public void createEmptyWorkbook(OutputStream os) {
        try {
            MetaModel metamodel = metamodelGenerator.generate();
            NewExcelFileGenerator.createEmptyXLS(os, metamodel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * New sheet generator: generates a new Excel file at the specified destination based on the current mapping.
     * @param fileDestination The path to the Excel file that is to be created
     */
    public void createEmptyWorkbook(String fileDestination) throws FileNotFoundException {
        createEmptyWorkbook(new FileOutputStream(fileDestination));
    }

    /**
     * Filled sheet generator: generates a new Excel file at the specified destination based on the current mapping and fills this with data from the Database.
     * @param os Stream to the Excel file that is to be created
     */
    public void createWorkbookWithDatabaseData(OutputStream os) {
        try {
            FilledExcelFileGenerator.createFilledExcelFile(os, entityManagerFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * Filled sheet generator: generates a new Excel file at the specified destination based on the current mapping and fills this with data from the Database.
     * @param fileDestination The path to the Excel file that is to be created
     */
    public void createWorkbookWithDatabaseData(String fileDestination) throws FileNotFoundException {
        createWorkbookWithDatabaseData(new FileOutputStream(fileDestination));
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

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

}
