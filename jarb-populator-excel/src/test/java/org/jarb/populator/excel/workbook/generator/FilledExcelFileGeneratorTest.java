package org.jarb.populator.excel.workbook.generator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.ExcelTestData;
import org.junit.Before;
import org.junit.Test;

public class FilledExcelFileGeneratorTest extends DefaultExcelTestDataCase {
    private ExcelTestData excelTestData;

    @Before
    public void setupFilledExcelFileTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        excelTestData = getExcelTestDataFactory().build();

        // For code coverage purposes:
        Constructor<FilledExcelFileGenerator> constructor = FilledExcelFileGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateFilledExcelFile() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException,
            InvalidFormatException {
        excelTestData.persistData("src/test/resources/Excel.xls");
        FilledExcelFileGenerator.createFilledExcelFile("src/test/resources/excel/generated/databaseunittest.xls", getEntityManagerFactory());
    }
}
