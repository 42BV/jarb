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

public class ColumnValidatorTest {

    private Workbook excel;

    @Before
    public void setupColumnValitorTest() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelVerification/Excel.xls"));

        //For code coverage purposes:
        Constructor<ColumnValidator> constructor = ColumnValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testValidateColumnsInSheet() {
        Set<String> columnNames = new HashSet<String>();
        List<String> messages = new ArrayList<String>();
        columnNames.add("id");
        columnNames.add("first_name");
        columnNames.add("salary_month");

        messages.add("Excel columns in worksheet [employees] match the mapping.");
        assertEquals(messages, ColumnValidator.validateColumnsInSheet(excel, columnNames, "employees"));
    }

    @Test
    public void testFindDifferencesBetweenColumnSets() {
        Set<String> excelColumnNames = new HashSet<String>();
        Set<String> columnNames = new HashSet<String>();
        List<String> messages = new ArrayList<String>();
        excelColumnNames.add("name");
        columnNames.add("name");
        columnNames.add("address");

        messages.add("Error in Excel worksheet [customers]: Sheet does not contain column [address] present in the mapping.");
        assertEquals(messages, ColumnValidator.findDifferencesBetweenColumnSets(excelColumnNames, columnNames, "customers"));
    }

}
