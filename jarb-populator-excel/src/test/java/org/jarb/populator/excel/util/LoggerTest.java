package org.jarb.populator.excel.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.persist.DataWriter;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.ExcelRowIntegration;
import org.jarb.populator.excel.mapping.importer.ExcelImporter;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

//Logger class is still a stub at this point 06-12-2010

public class LoggerTest extends DefaultExcelTestDataCase {

    private ClassDefinition<?> customer;
    private Logger logger;
    private Workbook excel;
    private List<ClassDefinition<?>> classDefinitions;

    @Before
    public void setUpLoggerTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        logger = new Logger();
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        classDefinitions = new ArrayList<ClassDefinition<?>>();

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        customer = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        customer.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(customer, excel));
        ClassDefinition<?> sla = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(),
                metamodel.entity(domain.entities.ServiceLevelAgreement.class), false);
        sla.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(sla, excel));
        classDefinitions.add(customer);
        classDefinitions.add(sla);
    }

    @Test
    public void testReportExportedInstances() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        //There's no way to check the output at this point, so we're just going to see if it doesnt generate an exception.
        Map<ClassDefinition<?>, Map<Integer, ExcelRow>> objectModel = ExcelImporter.parseExcel(excel, classDefinitions);
        DataWriter.saveEntity(DataWriter.createConnectionInstanceSet(ExcelRowIntegration.toMap(objectModel)), getEntityManagerFactory());
        logger.reportExportedInstances(objectModel);
    }

}
