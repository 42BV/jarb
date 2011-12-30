package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.JpaMetaModelGenerator;
import org.jarbframework.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

public class ForeignRelationsMapperTest extends DefaultExcelTestDataCase {

    private Workbook excel;

    @Before
    public void setUpExcelRecordTest() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<ForeignRelationsMapper> constructor = ForeignRelationsMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testMakeForeignRelations() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvalidFormatException, IOException,
            SecurityException, NoSuchFieldException {
        excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        EntityManagerFactory emf = getEntityManagerFactory();
        MetaModelGenerator mmg = new JpaMetaModelGenerator(emf);
        MetaModel medamodel = mmg.generate();

        Map<Definition, Map<Object, ExcelRow>> objectModel = new HashMap<Definition, Map<Object, ExcelRow>>();
        ExcelImporter excelImporter = new ExcelImporter(ValueConversionService.defaultConversions(), emf);

        List<Definition> entities = new ArrayList<Definition>(medamodel.entities());
        for (Definition entityDefinition : entities) {
            objectModel.put(entityDefinition, excelImporter.parseWorksheet(excel, entityDefinition));
        }

        for (Entry<Definition, Map<Object, ExcelRow>> classRecord : objectModel.entrySet()) {
            for (Entry<Object, ExcelRow> classValues : classRecord.getValue().entrySet()) {
                ExcelRow excelRow = classValues.getValue();
                Class<?> tobeTested = domain.entities.Project.class;
                if (excelRow.getCreatedInstance().getClass() == tobeTested) {
                    ForeignRelationsMapper.makeForeignRelations(excelRow, objectModel);
                    for (Entry<PropertyDefinition, Key> entry : excelRow.getValueMap().entrySet()) {
                        assertEquals(domain.entities.Customer.class, entry.getValue().getForeignClass());
                    }
                }
            }
        }
    }
}
