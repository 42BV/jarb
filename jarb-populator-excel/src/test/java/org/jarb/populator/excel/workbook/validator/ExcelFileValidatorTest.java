package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class ExcelFileValidatorTest extends DefaultExcelTestDataCase {

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

    @Before
    public void setupExcelFileValidatorTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvalidFormatException,
            IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        // For code coverage purposes:
        Constructor<DefaultExcelValidator> constructor = DefaultExcelValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
        validator = getExcelTestDataFactory().buildExcelValidator();
        metamodel = getExcelTestDataFactory().buildMetamodelGenerator().generate();
    }

    @Test
    public void testVerify1() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 1: Everything in order
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Excel columns in worksheet [employees] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("All the sheets specified in the mapping are present in the Excel file.");
        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase1.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify2() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 2: Missing column in Excel file
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Error in Excel worksheet [employees]: Sheet does not contain column [first_name] present in the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("All the sheets specified in the mapping are present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase2.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify3() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 3: Missing sheet in Excel file
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Excel columns in worksheet [employees] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("Error in Excel file. Worksheet [customers] from the mapping is not present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase3.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify4() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 4: Missing Employee sheet (because of the associative mapping, see if employees_projects) still tests ok.
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("Error in Excel file. Worksheet [employees] from the mapping is not present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase4.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify5() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 5: Extra column in Excel file (not available in mapping)
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Error in Excel worksheet [employees] : Mapping does not contain column [leased_car_model] present in the Excel sheet.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("All the sheets specified in the mapping are present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase5.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify6() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 6: Extra sheet in Excel file  (not available in mapping)
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Excel columns in worksheet [employees] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("Error in Excel file. Worksheet [ExtraSheet] is not present in the mapping.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase6.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

    @Test
    public void testVerify7() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 7: Extra column and missing column
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Error in Excel worksheet [employees] : Mapping does not contain column [leased_car_model] present in the Excel sheet.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Error in Excel worksheet [projects]: Sheet does not contain column [customer] present in the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Excel columns in worksheet [workspaces] match the mapping.");
        messages.add("All the sheets specified in the mapping are present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase7.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
        for (String message : messages) {
            System.out.println(message);
        }
    }

    @Test
    public void testVerify8() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        //Testcase 8: Missing embeddable column
        List<String> messages = new ArrayList<String>();
        messages.add("Excel columns in worksheet [NoTableAnnotation] match the mapping.");
        messages.add("Excel columns in worksheet [vipcustomers_gifts] match the mapping.");
        messages.add("Excel columns in worksheet [customers] match the mapping.");
        messages.add("Excel columns in worksheet [departments] match the mapping.");
        messages.add("Excel columns in worksheet [document_revisions] match the mapping.");
        messages.add("Excel columns in worksheet [documents] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects] match the mapping.");
        messages.add("Excel columns in worksheet [employees] match the mapping.");
        messages.add("Excel columns in worksheet [employees_projects_workspaces] match the mapping.");
        messages.add("Excel columns in worksheet [gifts] match the mapping.");
        messages.add("Excel columns in worksheet [projects] match the mapping.");
        messages.add("Excel columns in worksheet [releases] match the mapping.");
        messages.add("Excel columns in worksheet [sla] match the mapping.");
        messages.add("Excel columns in worksheet [vehicles] match the mapping.");
        messages.add("Error in Excel worksheet [workspaces]: Sheet does not contain column [\"invoice.city_name\"] present in the mapping.");
        messages.add("All the sheets specified in the mapping are present in the Excel file.");

        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Testcase8.xls"));
        assertEquals(messages, validator.validate(excel, metamodel).getMessages());
    }

}
