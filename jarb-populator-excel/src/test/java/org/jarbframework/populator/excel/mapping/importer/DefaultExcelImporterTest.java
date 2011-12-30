package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.generator.EntityDefinitionsGenerator;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

public class DefaultExcelImporterTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private List<Definition> classDefinitionList;
    private EntityDefinition<?> customer;
    private EntityDefinition<?> project;
    private EntityRegistry entityRegistry;
    private EntityDefinition<?> classDefinition;
    private Map<Object, ExcelRow> parseWorksheetMap;

    @Before
    public void setUpExcelImporterTest() throws InstantiationException, IllegalAccessException, InvalidFormatException, IOException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        classDefinitionList = new ArrayList<Definition>();

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> customerEntity = metamodel.entity(domain.entities.Customer.class);
        EntityType<?> projectEntity = metamodel.entity(domain.entities.Project.class);

        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        customer = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(customerEntity, false);
        project = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(projectEntity, false);
        classDefinitionList.add(customer);
        classDefinitionList.add(project);

        classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(customerEntity, false);
        parseWorksheetMap = new HashMap<Object, ExcelRow>();
    }

    @Test
    public void testParseExcel() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        entityRegistry = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory()).parseExcelToRegistry(excel,
                classDefinitionList);
        assertTrue(entityRegistry.all().size() > 0);
    }

    @Test
    public void testParseWorksheet() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        parseWorksheetMap = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory()).parseWorksheet(excel, classDefinition);
        assertTrue(parseWorksheetMap.keySet().contains(1));
        assertFalse(parseWorksheetMap.keySet().contains(2));
        assertTrue(parseWorksheetMap.keySet().contains(3));
        assertTrue(parseWorksheetMap.keySet().contains(4));
        assertTrue(parseWorksheetMap.keySet().contains(5));
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
        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(entity, true);
        parseWorksheetMap = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory()).parseWorksheet(excel, classDefinition);
    }

    @Test
    public void testDuplicatePrimaryKey() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/DuplicatePrimaryKey.xls"));
        parseWorksheetMap = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory()).parseWorksheet(excel, classDefinition);
        assertEquals(1, parseWorksheetMap.size());
    }
}
