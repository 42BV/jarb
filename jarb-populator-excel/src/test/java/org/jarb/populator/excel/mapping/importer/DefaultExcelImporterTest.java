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

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

public class DefaultExcelImporterTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private List<EntityDefinition<?>> classDefinitionList;
    private EntityDefinition<?> customer;
    private EntityDefinition<?> project;
    private Map<EntityDefinition<?>, Map<Object, ExcelRow>> parseExcelMap;
    private EntityDefinition<?> classDefinition;
    private Map<Object, ExcelRow> parseWorksheetMap;

    @Before
    public void setUpExcelImporterTest() throws InstantiationException, IllegalAccessException, InvalidFormatException, IOException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        classDefinitionList = new ArrayList<EntityDefinition<?>>();

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> customerEntity = metamodel.entity(domain.entities.Customer.class);
        EntityType<?> projectEntity = metamodel.entity(domain.entities.Project.class);

        customer = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), customerEntity, false);
        project = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), projectEntity, false);
        classDefinitionList.add(customer);
        classDefinitionList.add(project);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), customerEntity, false);
        parseWorksheetMap = new HashMap<Object, ExcelRow>();

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
        assertTrue(parseWorksheetMap.keySet().contains(1D));
        assertFalse(parseWorksheetMap.keySet().contains(2D));
        assertTrue(parseWorksheetMap.keySet().contains(3D));
        assertTrue(parseWorksheetMap.keySet().contains(4D));
        assertTrue(parseWorksheetMap.keySet().contains(5D));
    }

    /**
     * Whenever no type is defined, use the super class. Should not crash.
     */
    @Test
    public void testCannotFindDiscriminatorPosition() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException,
            NoSuchFieldException, ClassNotFoundException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/DiscriminatorColumnLacking.xls"));
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, true);
        parseWorksheetMap = ExcelImporter.parseWorksheet(excel, classDefinition);
    }

    @Test
    public void testDuplicatePrimaryKey() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/DuplicatePrimaryKey.xls"));
        parseWorksheetMap = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertEquals(1, parseWorksheetMap.size());
    }
}
