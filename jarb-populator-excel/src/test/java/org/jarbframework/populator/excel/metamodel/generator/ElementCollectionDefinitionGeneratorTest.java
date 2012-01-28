package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.mapping.importer.DefaultExcelImporterTest;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class ElementCollectionDefinitionGeneratorTest extends DefaultExcelImporterTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityDefinition<?> employee;

    @Before
    public void prepareTest() {
        entityManagerFactory = getEntityManagerFactory();
        EntityType<?> employeeType = entityManagerFactory.getMetamodel().entity(Employee.class);
        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(entityManagerFactory);
        employee = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(employeeType, false);
    }

    @Test
    public void testCreateDefinitionForSingleElementCollectionFromEntity() {
        PropertyDefinition phones = employee.property("phones");
        ElementCollectionDefinitionGenerator elementCollectionDefinitionGenerator = new ElementCollectionDefinitionGenerator(getEntityManagerFactory());
        EmbeddableElementCollectionDefinition<?> phonesDefinition = (EmbeddableElementCollectionDefinition<?>) elementCollectionDefinitionGenerator
                .createDefinitionForSingleElementCollectionFromEntity(phones);
        assertEquals(2, phonesDefinition.properties().size());
    }

}
