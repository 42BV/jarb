package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Test;

import domain.entities.Employee;

public class EmbeddedColumnGeneratorTest extends DefaultExcelTestDataCase {

    @Test
    public void testCreateColumnDefinitionsForEmbeddedField() throws NoSuchFieldException {
        EmbeddedColumnGenerator embeddedColumnGenerator = new EmbeddedColumnGenerator(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
        List<PropertyDefinition> generated = embeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(new PropertyReference(Employee.class, "address"));
        assertEquals("building_address", generated.get(0).getColumnName());
        assertEquals("city_name", generated.get(1).getColumnName());
    }

}
