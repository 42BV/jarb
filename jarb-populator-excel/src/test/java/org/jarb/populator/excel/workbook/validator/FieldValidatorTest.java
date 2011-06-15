package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;

public class FieldValidatorTest extends DefaultExcelTestDataCase {

    private ColumnDefinition columnDefinition;
    private ExcelRow excelRow;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        //For code coverage purposes:
        Constructor<FieldValidator> constructor = FieldValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testIsExistingRegularField() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        ClassDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, true);
        excelRow = new ExcelRow(classDefinition.getPersistentClass());
        columnDefinition = classDefinition.getColumnDefinitionByFieldName("startDate");
        assertTrue(FieldValidator.isExistingField(columnDefinition.getFieldName(), excelRow.getCreatedInstance().getClass()));
    }

    @Test
    public void testIsNotAnExistingRegularFieldAndSuperclassesNull() throws NoSuchFieldException {
        assertFalse(FieldValidator.isExistingField("test", domain.entities.Customer.class));
    }

}
