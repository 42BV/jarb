package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.WorkbookParser;
import org.junit.Before;
import org.junit.Test;

public class DefaultExcelValidatorTest extends DefaultExcelTestDataCase {

    /*
     * Because of the amount of options this class can generate, it has to be tested thourougly.
     * The situations we're going to test with the verify functions are:
     * 
     * 1) Everything in order
     * 2) Missing column in Excel file
     * 3) Missing sheet in Excel file
     * 4) Missing Employee sheet (because of the associative mapping, see if employees_projects) still tests ok.
     * 5) Extra column in Excel file (not available in mapping)
     * 6) Extra sheet in Excel file  (not available in mapping)
     * 7) Extra column and missing column
     * 8) Missing embeddable column
     * 
     */

    private WorkbookValidator validator;
    private MetaModel metamodel;
    
    private WorkbookParser parser;

    @Before
    public void setupExcelFileValidatorTest() {
        validator = new DefaultWorkbookValidator();
        metamodel = getExcelDataManagerFactory().buildMetamodelGenerator().generate();
        parser = getExcelDataManagerFactory().buildExcelParser();
    }

    @Test
    public void testSuccess() throws FileNotFoundException {
        //Testcase 1: Everything in order
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/valid.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void testMissingColumn() throws FileNotFoundException {
        //Testcase 2: Missing column in Excel file
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/missing_column.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> violations = validation.getSheetViolations("employees");
        assertEquals(1, violations.size());
        assertEquals("Column 'employees.employee_id' is missing.", violations.iterator().next().getMessage());
    }

    @Test
    public void testMissingSheet() throws FileNotFoundException {
        //Testcase 3: Missing sheet in Excel file
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/missing_sheet.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> violations = validation.getGlobalViolations();
        assertEquals(1, violations.size());
        assertEquals("Sheet 'customers' is missing.", violations.iterator().next().getMessage());
    }

    @Test
    public void testMissingSheetAssociateTableExists() throws FileNotFoundException {
        //Testcase 4: Missing Employee sheet (because of the associative mapping, see if employees_projects) still tests ok.
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/missing_sheet_with_associate.xls"));        
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> violations = validation.getGlobalViolations();
        assertEquals(1, violations.size());
        assertEquals("Sheet 'employees' is missing.", violations.iterator().next().getMessage());
    }

    @Test
    public void testUnknownColumn() throws FileNotFoundException {
        //Testcase 5: Extra column in Excel file (not available in mapping)
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/unknown_column.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> violations = validation.getSheetViolations("employees");
        assertEquals(1, violations.size());
        assertEquals("Column 'employees.leased_car_model' does not exist in our mapping.", violations.iterator().next().getMessage());
    }

    @Test
    public void testUnknownSheet() throws FileNotFoundException {
        //Testcase 6: Extra sheet in Excel file  (not available in mapping)
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/unknown_sheet.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> violations = validation.getGlobalViolations();
        assertEquals(1, violations.size());
        assertEquals("Sheet 'ExtraSheet' does not exist in our mapping.", violations.iterator().next().getMessage());
    }
    
  @Test
  public void testMissingEmbeddedColumn() throws FileNotFoundException {
      //Testcase 7: Missing embeddable column
      Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/missing_embedded_column.xls"));
      WorkbookValidationResult validation = validator.validate(excel, metamodel);
      assertTrue(validation.hasViolations());
      Set<WorkbookViolation> violations = validation.getSheetViolations("workspaces");
      assertEquals(1, violations.size());
      assertEquals("Column 'workspaces.\"invoice.city_name\"' is missing.", violations.iterator().next().getMessage());
  }

    @Test
    public void testUnknownAndMissingColumn() throws FileNotFoundException {
        //Testcase 8: Extra column and missing column
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/missing_and_unknown_column.xls"));
        WorkbookValidationResult validation = validator.validate(excel, metamodel);
        assertTrue(validation.hasViolations());
        Set<WorkbookViolation> employeeViolations = validation.getSheetViolations("employees");
        assertEquals(1, employeeViolations.size());
        assertEquals("Column 'employees.leased_car_model' does not exist in our mapping.", employeeViolations.iterator().next().getMessage());
        Set<WorkbookViolation> projectsViolations = validation.getSheetViolations("projects");
        assertEquals(1, projectsViolations.size());
        assertEquals("Column 'projects.customer' is missing.", projectsViolations.iterator().next().getMessage());
    }

}
