package org.jarb.populator.excel.mapping.excelrow;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.importer.ExcelImporter;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class ForeignRelationsMapperTest extends DefaultExcelTestDataCase {

    private Workbook excel;

    @Before
    public void setUpExcelRecordTest() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<ForeignRelationsMapper> constructor = ForeignRelationsMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /* TO FIX: Crashes if not equal to mapping */
    @Test
    public void testMakeForeignRelations() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvalidFormatException, IOException,
            SecurityException, NoSuchFieldException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        List<ClassDefinition> classDefinitions = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(classDefinitions, excel);

        Map<ClassDefinition, Map<Integer, ExcelRow>> objectModel = ExcelImporter.parseExcel(excel, classDefinitions);

        for (Entry<ClassDefinition, Map<Integer, ExcelRow>> classRecord : objectModel.entrySet()) {
            System.out.println(classRecord.getKey().getTableName());
            for (Entry<Integer, ExcelRow> classValues : classRecord.getValue().entrySet()) {
                ExcelRow excelRow = classValues.getValue();
                Class<?> tobeTested = domain.entities.Project.class;
                if (excelRow.getCreatedInstance().getClass() == tobeTested) {
                    ForeignRelationsMapper.makeForeignRelations(excelRow, objectModel);
                    // excelRow.getValueMap().containsValue("JoinColumnKey");
                    for (Entry<PropertyDefinition, Key> entry : excelRow.getValueMap().entrySet()) {
                        assertEquals(domain.entities.Customer.class, entry.getValue().getForeignClass());
                    }
                }
            }

        }
    }

}
