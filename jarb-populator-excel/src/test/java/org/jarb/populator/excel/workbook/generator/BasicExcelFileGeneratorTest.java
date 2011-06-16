package org.jarb.populator.excel.workbook.generator;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.ColumnType;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class BasicExcelFileGeneratorTest extends DefaultExcelTestDataCase {

    private ClassDefinition<?> classDefinition;

    @Before
    public void setUpEmptySheetGeneratorTest() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException,
            IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Employee.class, metamodel);
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        //For code coverage purposes:
        Constructor<BasicExcelFileGenerator> constructor = BasicExcelFileGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateTable() throws InstantiationException, IllegalAccessException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet workpage = BasicExcelFileGenerator.createTable(classDefinition, workbook);
        List<String> columnNames = new ArrayList<String>();
        columnNames.add("id");
        columnNames.add("date_of_birth");
        columnNames.add("building_address");
        columnNames.add("city_name");
        columnNames.add("first_name");
        columnNames.add("salary_month");
        columnNames.add("salary_scale");
        columnNames.add("company_vehicle_id");
        for (Iterator<Cell> it = workpage.getRow(0).cellIterator(); it.hasNext();) {
            Cell cell = it.next();
            assertTrue(cell.getStringCellValue() + " should not be stored.", columnNames.contains(cell.getStringCellValue()));
        }
    }

    @Test
    public void testCreateJoinTable() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Class<?> persistentClass = domain.entities.Employee.class;
        Field projectsField = persistentClass.getDeclaredField("projects");
        PropertyDefinition projectsDefinition = FieldAnalyzer.analyzeField(projectsField).build();

        List<String> columns = new ArrayList<String>();
        columns.add("employee_id");
        columns.add("project_id");

        BasicExcelFileGenerator.createJoinTable(projectsDefinition, workbook);
        for (Iterator<Cell> it = workbook.getSheet("employees_projects").getRow(0).cellIterator(); it.hasNext();) {
            Cell cell = it.next();
            assertTrue(columns.contains(cell.getStringCellValue()));
        }
    }

    @Test
    public void testWriteFile() throws InstantiationException, IllegalAccessException, IOException, InvalidFormatException {
        String path = "src/test/resources/excel/generated/BasicExcelFileGeneratorUnittest.xls";
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        HSSFWorkbook workbook = new HSSFWorkbook();
        @SuppressWarnings("unused")
        HSSFSheet workpage = BasicExcelFileGenerator.createTable(classDefinition, workbook);
        workbook.write(fileOutputStream);
        fileOutputStream.close();

        Workbook excel = new PoiExcelParser().parse(new FileInputStream(path));
        Collection<String> columnNamesAutomated = excel.getSheet(classDefinition.getTableName()).getColumnNames();

        Set<String> manual = new HashSet<String>();
        manual.add("id");
        manual.add("building_address");
        manual.add("city_name");
        manual.add("first_name");
        manual.add("salary_month");

        for (String columnName : manual) {
            assertTrue(columnName + " should have been stored.", columnNamesAutomated.contains(columnName));
        }
    }

    @Test
    public void testFailedCastToJoinTable() {
        for (PropertyDefinition columnDefinition : classDefinition.getPropertyDefinitions()) {
            if (columnDefinition.getColumnType() != ColumnType.JOIN_TABLE) {
                HSSFWorkbook workbook = new HSSFWorkbook();
                BasicExcelFileGenerator.createJoinTable(columnDefinition, workbook);
                break;
            }
        }
    }

}
