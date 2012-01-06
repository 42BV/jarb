package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ColumnDefinitionsGenerator;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import domain.entities.Employee;
import domain.entities.Phone;

public class StoreInversedJoinColumnTest extends DefaultExcelImporterTest {

    private EntityDefinition<Employee> employeeDefinition;
    private Workbook workbook;
    private ExcelRow excelRow;
    private InverseJoinColumnKey key;

    @Before
    public void setupStoreInversedJoinColumnTest() throws FileNotFoundException {
        Class<Employee> employee = Employee.class;
        EntityType<Employee> employeeEntity = getEntityManagerFactory().getMetamodel().entity(employee);

        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory());
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(schemaMapper);

        EntityDefinition.Builder<Employee> employeeBuilder = EntityDefinition.forClass(employee);
        employeeBuilder.setTableName("departments");
        employeeBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(new HashSet<EntityType<?>>(), employeeEntity, employee));
        employeeDefinition = employeeBuilder.build();

        workbook = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/TemporaryExcelFileForNewStyleElementCollections.xls"));
        excelRow = new ExcelRow(employee);

        key = new InverseJoinColumnKey();
        key.setForeignClass(Phone.class);
        HashMap<String, Object> keyValues = new HashMap<String, Object>();
        keyValues.put("employee_id", 8);
        key.setKeyValue(keyValues);
    }

    @Ignore
    @Test
    public void testStoreValue() {
        PropertyDefinition propertyDefinition = employeeDefinition.property("phones");
        StoreInversedJoinColumn.storeValue(workbook, employeeDefinition, propertyDefinition, 1, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(key));
    }

}
