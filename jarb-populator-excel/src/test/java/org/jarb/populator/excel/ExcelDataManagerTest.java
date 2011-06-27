package org.jarb.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    @Test
    public void testLoadByInputStream() throws FileNotFoundException {
        excelData.loadWorkbook(new FileInputStream("src/test/resources/Excel.xls")).continueIfValid();
    }
   
    @Test
    public void testLoadByResource() throws IOException {
        excelData.loadWorkbook(new ClassPathResource("Excel.xls")).continueIfValid();
    }
    
    @Test
    public void testLoadByString() throws FileNotFoundException {
        excelData.loadWorkbook("src/test/resources/Excel.xls").continueIfValid();
    }
    
    @Test
    public void testValidateWorkbook() throws FileNotFoundException {
        WorkbookValidationResult validation = excelData.loadWorkbook("src/test/resources/Excel.xls").validate();
        assertNotNull("No workbook validation was returned", validation);
        assertFalse("Workbook should not contain errors", validation.hasViolations());
    }
    
    @Test
    public void testInvalidWorkbook() throws FileNotFoundException {
        try {
            // Calling entities() should implictly invoke continueIfValid()
            excelData.loadWorkbook("src/test/resources/ExcelVerification/missing_sheet.xls").entities();
            fail("Expected an exception as the loaded workbook has an invalid structure");
        } catch(InvalidWorkbookException e) {
            WorkbookValidationResult validation = e.getValidation();
            assertNotNull("Expected a workbook validation with the exception", validation);
            assertEquals(1, validation.getViolations().size());
            assertEquals("Sheet 'customers' is missing.", validation.getViolations().iterator().next().getMessage());
            assertEquals("Worbook is invalid:\n - Sheet 'customers' is missing.", e.getMessage());
        }
    }
    
    @Test
    public void testPersistWorkbook() throws FileNotFoundException {
        excelData.loadWorkbook("src/test/resources/Excel.xls").persist();
    }
    
    @Test
    public void testCreateWorkbookTemplate() throws FileNotFoundException {
        excelData.newWorkbook().write("src/test/resources/excel/generated/NewExcelFile.xls");
        WorkbookValidationResult validation = excelData.loadWorkbook("src/test/resources/excel/generated/NewExcelFile.xls").validate();
        assertFalse("Created workbook template is not valid", validation.hasViolations());
    }

    @Test
    public void testCreateWorkbookWithData() throws FileNotFoundException {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        car.setId(42L);
        
        // Create a new workbook template, including our newly created car instance
        excelData.newWorkbook()
            .includeNewEntity(CompanyVehicle.class, 42L, car)
            .write("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
        
        // Read the generated workbook and check if the included entity was maintained
        CompanyVehicle result = excelData.loadWorkbook("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls")
            .entities()
                .find(CompanyVehicle.class, car.getId());
        
        assertNotNull("Car was not maintained during store and load.", result);
        assertEquals(car.getId(), result.getId());
    }

}
