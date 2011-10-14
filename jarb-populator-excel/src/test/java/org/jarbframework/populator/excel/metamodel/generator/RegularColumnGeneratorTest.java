package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Project;

public class RegularColumnGeneratorTest extends DefaultExcelTestDataCase {
    private RegularColumnGenerator regularColumnGenerator;

    @Before
    public void setUp() {
        regularColumnGenerator = new RegularColumnGenerator(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
    }

    @Test
    public void testCreateColumnDefinitionsForEmbeddedField() throws NoSuchFieldException {
        PropertyDefinition generated = regularColumnGenerator.createColumnDefinitionForRegularField(Project.class.getDeclaredField("name"), Project.class);
        assertEquals("name", generated.getColumnName());
    }

}
