package org.jarb.populator.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Before;
import org.junit.Test;

public class ExcelDataManagerTest extends DefaultExcelTestDataCase {
    private ExcelDataManager excelTestData;

    @Before
    public void setupETD() {
        excelTestData = getExcelDataManagerFactory().build();
    }

    @Test
    public void testProcessData() throws InvalidFormatException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            SecurityException, NoSuchFieldException {
        excelTestData.persistWorkbook("src/test/resources/Excel.xls");
    }

    @Test
    public void testNewSheetFilledWithDatabaseData() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException,
            NoSuchFieldException {
        excelTestData.createWorkbookWithDatabaseData("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
    }

    @Test
    public void testValidateSheet() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        excelTestData.validateWorkbook("src/test/resources/Excel.xls");
    }

    @Test
    public void testNewSheet() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        excelTestData.createEmptyWorkbook("src/test/resources/excel/generated/NewExcelFile.xls");
    }

}
