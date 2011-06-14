package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class DefaultExcelImporterTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private List<ClassDefinition<?>> classDefinitionList;
    private ClassDefinition<?> customer;
    private ClassDefinition<?> project;
    private Map<ClassDefinition<?>, Map<Integer, ExcelRow>> parseExcelMap;
    private ClassDefinition<?> classDefinition;
    private Map<Integer, ExcelRow> parseWorksheetMap;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setUpExcelImporterTest() throws InstantiationException, IllegalAccessException, InvalidFormatException, IOException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        classDefinitionList = new ArrayList<ClassDefinition<?>>();

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> customerEntity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);
        EntityType<?> projectEntity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        customer = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), customerEntity, false);
        project = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), projectEntity, false);
        classDefinitionList.add(customer);
        classDefinitionList.add(project);
        ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(classDefinitionList, excel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), customerEntity, false);
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
        parseWorksheetMap = new HashMap<Integer, ExcelRow>();

        //For code coverage purposes:
        Constructor<ExcelImporter> constructor = ExcelImporter.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testParseExcel() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        parseExcelMap = ExcelImporter.parseExcel(excel, classDefinitionList);
        assertTrue(classDefinitionList.containsAll(parseExcelMap.keySet()));
    }

    @Test
    public void testParseWorksheet() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        parseWorksheetMap = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertTrue(parseWorksheetMap.keySet().contains(1));
        assertFalse(parseWorksheetMap.keySet().contains(2));
        assertTrue(parseWorksheetMap.keySet().contains(3));
        assertTrue(parseWorksheetMap.keySet().contains(4));
        assertTrue(parseWorksheetMap.keySet().contains(5));
    }

    @Test
    public void testCannotFindDiscriminatorPosition() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException,
            NoSuchFieldException, ClassNotFoundException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/DiscriminatorColumnLacking.xls"));
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, true);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));
        parseWorksheetMap = ExcelImporter.parseWorksheet(excel, classDefinition);
        for (Entry<Integer, ExcelRow> entry : parseWorksheetMap.entrySet()) {
            for (PropertyDefinition columnDefinition : entry.getValue().getValueMap().keySet()) {
                assertFalse(columnDefinition.isDiscriminatorColumn());
            }
        }
    }

    @Test
    public void testDuplicatePrimaryKey() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/DuplicatePrimaryKey.xls"));
        parseWorksheetMap = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertEquals(1, parseWorksheetMap.size());
    }
}
