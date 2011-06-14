package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class WorksheetDefinitionTest extends DefaultExcelTestDataCase {

    private ClassDefinition<?> classDefinition;
    private WorksheetDefinition worksheetDefinition;
    private Workbook excel;

    @Before
    public void setUpWorksheetDefinition() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        worksheetDefinition = new WorksheetDefinition();
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));
    }

    @Test
    public void testAnalyzeWorksheet() {
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        assertEquals(0, (int) worksheetDefinition.getColumnPosition("id"));
        assertEquals(1, (int) worksheetDefinition.getColumnPosition("first_name"));
        assertEquals(2, (int) worksheetDefinition.getColumnPosition("company_name"));
    }

    @Test
    public void testAddGetColumnPosition() {
        //Add a columnPosition where addressColumn is not null
        Integer addressColumn = 3;
        worksheetDefinition.addColumnPosition("address", "Worksheet1", addressColumn);
        assertEquals(addressColumn, worksheetDefinition.getColumnPosition("address"));

        //Add a columnPosition where addressColumn is null
        worksheetDefinition.addColumnPosition("address2", "Worksheet1", null);
        assertEquals(null, worksheetDefinition.getColumnPosition("address2"));
    }

}
