package org.jarb.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;

public class ExcelDataManagerTest extends DefaultExcelTestDataCase {
    private ExcelDataManager excelData;

    @Before
    public void setUp() {
        excelData = getExcelDataManager();
    }

    /**
     * Read the workbook using a regular input stream.
     */
    @Test
    public void testLoadByInputStream() throws FileNotFoundException {
        excelData.loadWorkbook(new FileInputStream("src/test/resources/Excel.xls")).continueIfValid();
    }

    /**
     * Read the workbook with a spring resource.
     * @throws IOException
     */
    @Test
    public void testLoadByResource() throws IOException {
        excelData.loadWorkbook(new ClassPathResource("Excel.xls")).continueIfValid();
    }

    /**
     * Read the workbook with a string based file path.
     */
    @Test
    public void testLoadByString() throws FileNotFoundException {
        excelData.loadWorkbook("src/test/resources/Excel.xls").continueIfValid();
    }

    // Validation

    /**
     * Ensure that correct workbooks are recognized as valid.
     */
    @Test
    public void testValidateWorkbook() throws FileNotFoundException {
        WorkbookValidationResult validation = excelData.loadWorkbook("src/test/resources/Excel.xls").validate();
        assertNotNull("No workbook validation was returned", validation);
        assertFalse("Workbook should not contain errors", validation.hasViolations());
    }

    /**
     * And invalid workbooks are detected. Calling entities() should implicitly
     * invoke continueIfValid(), meaning we can only read entities whenever the
     * entire workbook is valid.
     */
    @Test
    public void testInvalidWorkbook() throws FileNotFoundException {
        try {
            excelData.loadWorkbook("src/test/resources/ExcelVerification/missing_sheet.xls").entities();
            fail("Expected an exception as the loaded workbook has an invalid structure");
        } catch (InvalidWorkbookException e) {
            WorkbookValidationResult validation = e.getValidation();
            assertNotNull("Expected a workbook validation with the exception", validation);
            assertEquals(1, validation.getViolations().size());
            assertEquals("Sheet 'customers' is missing.", validation.getViolations().iterator().next().getMessage());
            assertEquals("Worbook is invalid:\n - Sheet 'customers' is missing.", e.getMessage());
        }
    }

    // Persisting

    /**
     * Entities retrieved from the workbook can be persisted.
     */
    @Test
    public void testPersistWorkbook() throws FileNotFoundException {
        excelData.persist(excelData.loadWorkbook("src/test/resources/Excel.xls"));
    }

    // Building

    @Test
    public void testCreateWorkbookTemplate() throws FileNotFoundException {
        excelData.newWorkbook().write("src/test/resources/excel/generated/NewExcelFile.xls");
        WorkbookValidationResult validation = excelData.loadWorkbook("src/test/resources/excel/generated/NewExcelFile.xls").validate();
        assertFalse("Created workbook template is not valid", validation.hasViolations());
    }

    @Test
    public void testCreateWorkbookWithData() throws FileNotFoundException {
        CompanyCar bugatti = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        bugatti.setId(42L);

        final String outputFilePath = "src/test/resources/excel/generated/NewExcelFileFromDatabase.xls";
        excelData.newWorkbook().include(CompanyVehicle.class, 42L, bugatti).write(outputFilePath);

        CompanyVehicle result = excelData.loadWorkbook(outputFilePath).entities().find(CompanyVehicle.class, bugatti.getId());
        assertNotNull("Car was not maintained during store and load.", result);
        assertEquals(bugatti.getId(), result.getId());
    }

    @Test
    public void testCloneAndEnhanceWorkbook() throws FileNotFoundException {
        CompanyCar bugatti = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        bugatti.setId(42L);

        final String outputFilePath = "src/test/resources/excel/generated/Clone.xls";
        EntityRegistry entities = excelData.loadWorkbook("src/test/resources/Excel.xls").entities();
        excelData.newWorkbook(entities).include(CompanyVehicle.class, 42L, bugatti).write(outputFilePath);

        CompanyVehicle result = excelData.loadWorkbook(outputFilePath).entities().find(CompanyVehicle.class, bugatti.getId());
        assertNotNull("Car was not maintained during store and load.", result);
        assertEquals(bugatti.getId(), result.getId());
    }

}
