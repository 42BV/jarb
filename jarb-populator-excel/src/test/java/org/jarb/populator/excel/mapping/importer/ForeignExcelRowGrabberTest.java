package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import domain.entities.Customer;

public class ForeignExcelRowGrabberTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private EntityDefinition<?> classDefinition;
    private Class<?> persistentClass;
    private Map<Object, ExcelRow> objectModel;

    @Before
    public void setUpExcelRecordTest() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<ForeignExcelRowGrabber> constructor = ForeignExcelRowGrabber.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetInstanceValueSingular() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException,
            ClassNotFoundException {
        persistentClass = domain.entities.Project.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        Key keyValue = new JoinColumnKey();
        keyValue.setForeignClass(Customer.class);
        keyValue.setKeyValue(6D);
        objectModel = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertEquals(persistentClass, ForeignExcelRowGrabber.getInstanceValue(keyValue, objectModel).getClass());
    }

    @Test
    public void testGetInstanceValuePlural() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException,
            ClassNotFoundException {
        persistentClass = domain.entities.Project.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        Key key = new JoinTableKey();
        key.setForeignClass(Customer.class);

        Set<Integer> keyValueSet = new HashSet<Integer>();

        for (Integer i = 6; i <= 8; i++) {
            if (i != null) {
                keyValueSet.add(i);
            }
        }

        key.setKeyValue(keyValueSet);
        objectModel = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertFalse(ForeignExcelRowGrabber.getInstanceValue(key, objectModel).equals(null));
    }

    @Test
    public void testGetInstanceValueNull() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException,
            ClassNotFoundException {
        persistentClass = domain.entities.Project.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        Key key = null;
        objectModel = ExcelImporter.parseWorksheet(excel, classDefinition);
        assertEquals(null, ForeignExcelRowGrabber.getInstanceValue(key, objectModel));
    }

    @Test
    public void joinColumnKeyNotFound() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {

        Class<?> foreignExcelRowGrabber = ForeignExcelRowGrabber.class;
        Class<?>[] paramTypes = { Object.class, java.util.Map.class };
        Object[] parameters = { 1D, new HashMap<Integer, ExcelRow>() };
        Method getInstanceByJoinColumnKey = foreignExcelRowGrabber.getDeclaredMethod("getInstanceByJoinColumnKey", paramTypes);
        getInstanceByJoinColumnKey.setAccessible(true);
        assertEquals(null, getInstanceByJoinColumnKey.invoke(null, parameters));

    }

    @Test
    public void joinTableKeyNotFound() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
            NoSuchMethodException {
        Set<Integer> intSet = new HashSet<Integer>();
        intSet.add(1);
        Key joinTableKey = new JoinTableKey();
        joinTableKey.setForeignClass(domain.entities.Customer.class);
        joinTableKey.setKeyValue(intSet);

        Class<?> foreignExcelRowGrabber = ForeignExcelRowGrabber.class;
        Class<?>[] paramTypes = { Key.class, java.util.Map.class };
        Object[] parameters = { joinTableKey, new HashMap<Integer, ExcelRow>() };
        Method getInstanceByJoinTableKey = foreignExcelRowGrabber.getDeclaredMethod("getInstancesByJoinTableKey", paramTypes);
        getInstanceByJoinTableKey.setAccessible(true);
        assertEquals(new HashSet<Object>(), getInstanceByJoinTableKey.invoke(null, parameters));

    }

}
