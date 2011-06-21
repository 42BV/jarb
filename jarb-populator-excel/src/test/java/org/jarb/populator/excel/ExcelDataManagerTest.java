package org.jarb.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.jarb.populator.excel.entity.EntityRegistry;
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
        InputStream is = new FileInputStream("src/test/resources/Excel.xls");
        excelDataManager.persistWorkbook(is);
        
        // TODO: Assert that entities are in the database
    }
    
    @Test
    public void testValidateWorkbook() throws FileNotFoundException {
        InputStream is = new FileInputStream("src/test/resources/Excel.xls");
        WorkbookValidation result = excelDataManager.validateWorkbook(is);
        
        assertNotNull(result);
    }
    
    @Test
    public void testCreateWorkbookTemplate() throws FileNotFoundException {
        OutputStream os = new FileOutputStream("src/test/resources/excel/generated/NewExcelFile.xls");
        excelDataManager.createWorkbookTemplate(os);
        
        InputStream is = new FileInputStream("src/test/resources/excel/generated/NewExcelFile.xls");
        WorkbookValidation validation = excelDataManager.validateWorkbook(is);

        assertTrue(validation.getMissingSheets().isEmpty());
    }

    @Test
    public void testCreateWorkbookWithDatabaseData() throws FileNotFoundException {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        car.setId(42L);
        EntityRegistry registry = new EntityRegistry();
        registry.add(CompanyVehicle.class, 42L, car);
        
        OutputStream os = new FileOutputStream("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
        excelDataManager.createWorkbookWithData(os, registry);
        
        InputStream is = new FileInputStream("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
        WorkbookValidation validation = excelDataManager.validateWorkbook(is);
        assertTrue(validation.getMissingSheets().isEmpty());

        is = new FileInputStream("src/test/resources/excel/generated/NewExcelFileFromDatabase.xls");
        EntityRegistry resultRegistry = excelDataManager.loadWorkbook(is);
        CompanyVehicle resultCar = resultRegistry.get(CompanyVehicle.class, car.getId());
        assertNotNull("Car was not maintained during export.", resultCar);
        assertEquals(car.getId(), resultCar.getId());
    }

}
