package org.jarbframework.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ColumnDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Phone;

public class EmbeddableElementCollectionDefinitionTest extends DefaultExcelTestDataCase {

    private Class<Phone> phone = Phone.class;
    private EmbeddableElementCollectionDefinition<Phone> phoneDefinition;

    @Before
    public void prepareTest() {
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(getEntityManagerFactory());
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EmbeddableElementCollectionDefinition.Builder<Phone> phoneBuilder = EmbeddableElementCollectionDefinition.forClass(phone);
        phoneBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(metamodel.embeddable(phone), phone));
        phoneDefinition = phoneBuilder.build();
    }

    @Test
    public void testGetColumnNames() {
        Set<String> columnNames = new HashSet<String>();
        columnNames.add("phone_model");
        columnNames.add("phone_number");
        assertEquals(columnNames, phoneDefinition.getColumnNames());
    }

}
