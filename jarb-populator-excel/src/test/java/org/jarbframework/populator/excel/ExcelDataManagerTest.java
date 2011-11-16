package org.jarbframework.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.entity.EntityTable;
import org.jarbframework.populator.excel.entity.persist.EntityWriter;
import org.jarbframework.populator.excel.entity.persist.JpaEntityWriter;
import org.jarbframework.populator.excel.workbook.validator.WorkbookValidationResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;
import domain.entities.Employee;
import domain.entities.Phone;

public class ExcelDataManagerTest extends DefaultExcelTestDataCase {
    private ExcelDataManager excelDataManager;

    @Before
    public void setUp() {
        excelDataManager = getExcelDataManager();
    }

    /**
     * Read the workbook using a regular input stream.
     */
    @Test
    public void testLoadByInputStream() throws FileNotFoundException {
        excelDataManager.load(new FileInputStream("src/test/resources/Excel.xls")).continueIfValid();
    }

    /**
     * Read the workbook with a spring resource.
     * @throws IOException
     */
    @Test
    public void testLoadByResource() throws IOException {
        excelDataManager.load(new ClassPathResource("Excel.xls")).continueIfValid();
    }

    /**
     * Read the workbook with a string based file path.
     */
    @Test
    public void testLoadByString() throws FileNotFoundException {
        excelDataManager.load("src/test/resources/Excel.xls").continueIfValid();
    }

    // Validation

    /**
     * Ensure that correct workbooks are recognized as valid.
     */
    @Test
    public void testValidateWorkbook() throws FileNotFoundException {
        WorkbookValidationResult validation = excelDataManager.load("src/test/resources/Excel.xls").validate();
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
            excelDataManager.load("src/test/resources/ExcelVerification/missing_sheet.xls").entities();
            fail("Expected an exception as the loaded workbook has an invalid structure");
        } catch (InvalidWorkbookException e) {
            assertEquals("Workbook is invalid:\n - Sheet 'customers' is missing.", e.getMessage());
        }
    }

    @Test
    public void testNonStrictValidation() throws FileNotFoundException {
        excelDataManager.setStrict(false);
        excelDataManager.load("src/test/resources/ExcelVerification/missing_sheet.xls").continueIfValid();
        excelDataManager.setStrict(true);
    }

    @Test
    public void testNonStrictValidationWithError() throws FileNotFoundException {
        excelDataManager.setStrict(false);
        try {
            excelDataManager.load("src/test/resources/ExcelVerification/missing_identifier.xls").entities();
            fail("Expected an exception as the loaded workbook has an invalid structure");
        } catch (InvalidWorkbookException e) {
            assertEquals("Workbook is invalid:\n - Row 1 in 'currencies' has no identifier.", e.getMessage());
        }
        excelDataManager.setStrict(true);
    }

    // Persisting

    /**
     * Entities retrieved from the workbook can be persisted.
     */
    @Test
    public void testPersistWorkbook() throws FileNotFoundException {
        excelDataManager.load("src/test/resources/Excel.xls").persist();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testPersistElementCollection() {
        Employee henk = new Employee();
        henk.setName("Henk");

        Phone htc = new Phone();
        htc.setPhoneModel("HTC Desire");
        htc.setPhoneNumber("0612345678");

        Phone iPhone = new Phone();
        iPhone.setPhoneModel("iPhone 3GS");
        iPhone.setPhoneNumber("0687654321");

        List<Phone> phoneList = new ArrayList<Phone>();
        phoneList.add(htc);
        phoneList.add(iPhone);

        henk.setPhones(phoneList);

        EntityWriter entityWriter = new JpaEntityWriter(getEntityManagerFactory());

        EntityRegistry entReg = new EntityRegistry();

        Class employeeClass = domain.entities.Employee.class;
        EntityTable<Object> employees = new EntityTable<Object>(employeeClass);

        Object identifier = UUID.randomUUID().toString();
        employees.add(identifier, henk);

        entReg.addAll(employees);
        entityWriter.persist(entReg);
    }

    // Building

    @Test
    public void testCreateWorkbookTemplate() throws FileNotFoundException {
        excelDataManager.builder().write("src/test/resources/excel/generated/NewExcelFile.xls");
        WorkbookValidationResult validation = excelDataManager.load("src/test/resources/excel/generated/NewExcelFile.xls").validate();
        assertFalse("Created workbook template is not valid", validation.hasViolations());
    }

    @Test
    public void testCreateWorkbookWithData() throws FileNotFoundException {
        CompanyCar bugatti = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        bugatti.setId(42L);

        final String outputFilePath = "src/test/resources/excel/generated/NewExcelFileFromDatabase.xls";
        excelDataManager.builder().include(CompanyVehicle.class, 42L, bugatti).write(outputFilePath);
        
        CompanyVehicle result = excelDataManager.load(outputFilePath).entities().find(CompanyVehicle.class, bugatti.getId());
        assertNotNull("Car was not maintained during store and load.", result);
        assertEquals(bugatti.getId(), result.getId());
    }

    @Test
    public void testCloneAndEnhanceWorkbook() throws FileNotFoundException {
        CompanyCar bugatti = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        bugatti.setId(42L);

        final String outputFilePath = "src/test/resources/excel/generated/Clone.xls";
        EntityRegistry entities = excelDataManager.load("src/test/resources/Excel.xls").entities();
        excelDataManager.builder(entities).include(CompanyVehicle.class, 42L, bugatti).write(outputFilePath);

        CompanyVehicle result = excelDataManager.load(outputFilePath).entities().find(CompanyVehicle.class, bugatti.getId());
        assertNotNull("Car was not maintained during store and load.", result);
        assertEquals(bugatti.getId(), result.getId());
    }

}
