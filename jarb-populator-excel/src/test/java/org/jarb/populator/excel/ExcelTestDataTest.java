package org.jarb.populator.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Before;
import org.junit.Test;

public class ExcelTestDataTest extends DefaultExcelTestDataCase {
    private ExcelTestData excelTestData;

    @Before
    public void setupETD() {
        excelTestData = getExcelTestDataFactory().build();
    }

    @Test
    public void testProcessData() throws InvalidFormatException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            SecurityException, NoSuchFieldException {
        excelTestData.persistData("src/test/resources/Excel.xls");
    }

    @Test
    public void testNewSheetFilledWithDatabaseData() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException,
            NoSuchFieldException {
        excelTestData.newSheetFilledWithDatabaseData("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
    }

    @Test
    public void testValidateSheet() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        excelTestData.validateSheet("src/test/resources/Excel.xls");
    }

    @Test
    public void testNewSheet() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        excelTestData.newSheet("src/test/resources/excel/generated/NewExcelFile.xls");
    }

}
