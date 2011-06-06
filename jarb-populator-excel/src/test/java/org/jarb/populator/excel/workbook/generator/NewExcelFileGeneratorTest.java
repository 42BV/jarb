package org.jarb.populator.excel.workbook.generator;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.util.ClassDefinitionNameComparator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class NewExcelFileGeneratorTest extends DefaultExcelTestDataCase {

    private Workbook excelEmpty;
    private Workbook excelRegular;

    @Before
    public void setUpEmptySheetGeneratorTest() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException,
            IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {

        //For code coverage purposes:
        Constructor<NewExcelFileGenerator> constructor = NewExcelFileGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();

    }

    /*
     * Test all the void functions in EmptySheetGenerator, reads the written data again and compares it with the regular Excel file.
     * TO FIX: Crashes if excelRegular is not in sync with metamodel
     */
    @Test
    public void testCreateXLSAndSubfunctions() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException,
            InvalidFormatException, SecurityException, NoSuchFieldException {
        NewExcelFileGenerator.createEmptyXLS("src/test/resources/excel/generated/ExcelEmpty.xls", getEntityManagerFactory());
        excelEmpty = new PoiExcelParser().parse(new FileInputStream("src/test/resources/excel/generated/ExcelEmpty.xls"));
        excelRegular = new PoiExcelParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        List<ClassDefinition> classDefinitionsEmpty = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(classDefinitionsEmpty, excelEmpty);
        Collections.sort(classDefinitionsEmpty, new ClassDefinitionNameComparator());

        List<ClassDefinition> classDefinitionsRegular = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(classDefinitionsRegular, excelRegular);
        Collections.sort(classDefinitionsRegular, new ClassDefinitionNameComparator());

        for (int i = 0; i < classDefinitionsEmpty.size(); i++) {
            ClassDefinition empty = classDefinitionsEmpty.get(i);
            ClassDefinition regular = classDefinitionsRegular.get(i);
            for (int a = 0; a < empty.getColumnDefinitions().size(); a++) {
                assertEquals(empty.getColumnDefinitions().get(a).getColumnName(), (regular.getColumnDefinitions().get(a).getColumnName()));
            }
        }

        for (int i = 0; i < classDefinitionsRegular.size(); i++) {
            ClassDefinition empty = classDefinitionsEmpty.get(i);
            ClassDefinition regular = classDefinitionsRegular.get(i);
            for (int a = 0; a < regular.getColumnDefinitions().size(); a++) {
                assertEquals(empty.getColumnDefinitions().get(a).getColumnName(), (regular.getColumnDefinitions().get(a).getColumnName()));
            }
        }

    }
}
