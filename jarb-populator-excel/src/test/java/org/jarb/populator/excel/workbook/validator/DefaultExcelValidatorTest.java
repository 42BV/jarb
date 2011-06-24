package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.ExcelParser;
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

    private ExcelValidator validator;
    private MetaModel metamodel;
    
    private ExcelParser parser;

    @Before
    public void setupExcelFileValidatorTest() {
        validator = new DefaultExcelValidator();
        metamodel = getExcelDataManagerFactory().buildMetamodelGenerator().generate();
        parser = getExcelDataManagerFactory().buildExcelParser();
    }

    @Test
    public void testSuccess() throws FileNotFoundException {
        //Testcase 1: Everything in order
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase1.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getMissingSheets().isEmpty());
    }

    @Test
    public void testMissingColumn() throws FileNotFoundException {
        //Testcase 2: Missing column in Excel file
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase2.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        
        assertTrue(validation.getMissingSheets().isEmpty());
        assertTrue(validation.getSheetValidation("employees").getMissingColumns().contains("first_name"));
    }

    @Test
    public void testMissingSheet() throws FileNotFoundException {
        //Testcase 3: Missing sheet in Excel file
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase3.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getMissingSheets().contains("customers"));
    }

    @Test
    public void testMissingSheetAssociateTableExists() throws FileNotFoundException {
        //Testcase 4: Missing Employee sheet (because of the associative mapping, see if employees_projects) still tests ok.
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase4.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getMissingSheets().contains("employees"));
        assertFalse(validation.getMissingSheets().contains("employees_projects"));
    }

    @Test
    public void testUnknownColumn() throws FileNotFoundException {
        //Testcase 5: Extra column in Excel file (not available in mapping)
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase5.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        SheetValidation employeeValidation = validation.getSheetValidation("employees");
        assertTrue(employeeValidation.getUnknownColumns().contains("leased_car_model"));
    }

    @Test
    public void testUnknownSheet() throws FileNotFoundException {
        //Testcase 6: Extra sheet in Excel file  (not available in mapping)
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase6.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getUnknownSheets().contains("ExtraSheet"));
    }

    @Test
    public void testUnknownAndMissingColumn() throws FileNotFoundException {
        //Testcase 7: Extra column and missing column
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase7.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getSheetValidation("employees").getUnknownColumns().contains("leased_car_model"));
        assertTrue(validation.getSheetValidation("projects").getMissingColumns().contains("customer"));
    }

    @Test
    public void testMissingEmbeddedColumn() throws FileNotFoundException {
        //Testcase 8: Missing embeddable column
        Workbook excel = parser.parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase8.xls"));
        WorkbookValidation validation = validator.validate(excel, metamodel);
        assertTrue(validation.getSheetValidation("workspaces").getMissingColumns().contains("\"invoice.city_name\""));
    }

}
