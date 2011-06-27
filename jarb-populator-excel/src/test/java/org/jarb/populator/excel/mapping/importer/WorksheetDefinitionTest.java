package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class WorksheetDefinitionTest extends DefaultExcelTestDataCase {

    private EntityDefinition<?> classDefinition;
    private WorksheetDefinition worksheetDefinition;
    private Workbook excel;

    @Before
    public void setUpWorksheetDefinition() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        worksheetDefinition = new WorksheetDefinition();
        excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        classDefinition = getExcelDataManagerFactory().buildMetamodelGenerator().generate().entity(Customer.class);
    }

    @Test
    public void testAnalyzeWorksheet() {
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
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
