package org.jarb.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.jarb.populator.excel.workbook.validator.WorkbookValidation;
import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;

public class ExcelDataManagerTest extends DefaultExcelTestDataCase {
    private ExcelDataManager excelDataManager;

    @Before
    public void setUp() {
        excelDataManager = getExcelDataManager();
    }

    @Test
    public void testPersistWorkbook() throws FileNotFoundException {
        excelDataManager.load("src/test/resources/Excel.xls").persist();
    }
    
    @Test
    public void testValidateWorkbook() throws FileNotFoundException {
        WorkbookValidation result = excelDataManager.load("src/test/resources/Excel.xls").validate();
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }
    
    @Test
    public void testCreateWorkbookTemplate() throws FileNotFoundException {
        excelDataManager.buildWorkbook().write("src/test/resources/excel/generated/NewExcelFile.xls");
        assertFalse(excelDataManager.load("src/test/resources/excel/generated/NewExcelFile.xls").validate().hasErrors());
    }

    @Test
    public void testCreateWorkbookWithDatabaseData() throws FileNotFoundException {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        car.setId(42L);
        
        excelDataManager.buildWorkbook()
            .includeEntity(CompanyVehicle.class, 42L, car)
            .write("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
        
        
        CompanyVehicle result = excelDataManager
            .load("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls")
                .continueIfValid().entities()
                    .get(CompanyVehicle.class, car.getId());
        assertNotNull("Car was not maintained during store() -> load().", result);
        assertEquals(car.getId(), result.getId());
    }

}
