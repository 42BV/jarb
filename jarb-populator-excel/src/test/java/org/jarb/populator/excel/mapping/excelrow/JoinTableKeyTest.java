package org.jarb.populator.excel.mapping.excelrow;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class JoinTableKeyTest extends DefaultExcelTestDataCase {

    private ClassDefinition classDefinition;
    private Workbook excel;
    private Key key;
    private Set<Integer> keyValueSet;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setUpJoinTableKeyTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        key = new JoinTableKey();
        keyValueSet = new HashSet<Integer>();
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);

        for (Integer i = 6; i <= 8; i++) {
            if (i != null) {
                keyValueSet.add(i);
            }
        }
    }

    @Test
    public void testSetGetKeyValues() {
        //Set the key value of the key object to the set that has just been filled
        key.setKeyValue(keyValueSet);

        //Retrieve the key values again and check if they match the ones in keyValueSet
        assertEquals(keyValueSet, ((JoinTableKey) key).getKeyValues());
    }

}
