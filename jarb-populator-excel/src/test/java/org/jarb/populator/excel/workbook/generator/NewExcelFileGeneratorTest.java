package org.jarb.populator.excel.workbook.generator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ClassDefinitionNameComparator;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;

public class NewExcelFileGeneratorTest extends DefaultExcelTestDataCase {

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
        NewExcelFileGenerator.createEmptyXLS("src/test/resources/excel/generated/ExcelEmpty.xls", generateMetamodel());

        List<ClassDefinition<?>> classDefinitionsEmpty = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        Collections.sort(classDefinitionsEmpty, new ClassDefinitionNameComparator());

        List<ClassDefinition<?>> classDefinitionsRegular = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        Collections.sort(classDefinitionsRegular, new ClassDefinitionNameComparator());

        for (int i = 0; i < classDefinitionsEmpty.size(); i++) {
            ClassDefinition<?> empty = classDefinitionsEmpty.get(i);
            ClassDefinition<?> regular = classDefinitionsRegular.get(i);
            for (int a = 0; a < empty.getPropertyDefinitions().size(); a++) {
                assertEquals(empty.getPropertyDefinitions().get(a).getColumnName(), (regular.getPropertyDefinitions().get(a).getColumnName()));
            }
        }

        for (int i = 0; i < classDefinitionsRegular.size(); i++) {
            ClassDefinition<?> empty = classDefinitionsEmpty.get(i);
            ClassDefinition<?> regular = classDefinitionsRegular.get(i);
            for (int a = 0; a < regular.getPropertyDefinitions().size(); a++) {
                assertEquals(empty.getPropertyDefinitions().get(a).getColumnName(), (regular.getPropertyDefinitions().get(a).getColumnName()));
            }
        }

    }
}
