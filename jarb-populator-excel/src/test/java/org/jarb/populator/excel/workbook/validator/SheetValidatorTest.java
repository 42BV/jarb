package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class SheetValidatorTest {

    private Workbook excel;

    @Before
    public void setupSheetValidatorTest() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Excel.xls"));

        //For code coverage purposes:
        Constructor<SheetValidator> constructor = SheetValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCheckSheetAvailability() throws InvalidFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        Set<String> sheetNames = new HashSet<String>();
        List<String> messages = new ArrayList<String>();

        sheetNames.add("customers");
        sheetNames.add("employees");
        sheetNames.add("projects");
        sheetNames.add("employees_projects");

        messages.add("All the sheets specified in the mapping are present in the Excel file.");
        assertEquals(messages, SheetValidator.checkSheetAvailability(excel, sheetNames));
    }

    @Test
    public void testFindDifferencesBetweenSheetSets() {
        Set<String> excelSheetNames = new HashSet<String>();
        Set<String> mappingSheetNames = new HashSet<String>();
        List<String> messages = new ArrayList<String>();
        excelSheetNames.add("customers");
        mappingSheetNames.add("customers");
        mappingSheetNames.add("projects");

        messages.add("Error in Excel file. Worksheet [projects] from the mapping is not present in the Excel file.");
        assertEquals(messages, SheetValidator.findDifferencesBetweenSheetSets(excelSheetNames, mappingSheetNames));
    }

}
