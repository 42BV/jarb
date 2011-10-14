package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.Department;
import domain.entities.Employee;

public class ColumnDefinitionsGeneratorTest extends DefaultExcelTestDataCase {
    private ColumnDefinitionsGenerator columnDefinitionsGenerator;

    @Before
    public void setUp() {
        columnDefinitionsGenerator = new ColumnDefinitionsGenerator(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
    }

    @Test
    public void testCreateColumnDefinitions() throws NoSuchFieldException {
        Class<Department> persistentClass = Department.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();

        EntityDefinition.Builder<Department> classDefinitionBuilder = EntityDefinition.forClass(Department.class).setTableName("departments");

        classDefinitionBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(persistentClass.getDeclaredField("departmentName"), classDefinitionBuilder.build().property("departmentName").getField());
    }

    @Test
    public void testCreateColumnDefinitionsWithEmbeddables() throws NoSuchFieldException {
        Class<Employee> persistentClass = Employee.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();

        EntityDefinition.Builder<Employee> classDefinitionBuilder = EntityDefinition.forClass(Employee.class).setTableName("employees");
        classDefinitionBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(Address.class.getDeclaredField("streetAndNumber"), classDefinitionBuilder.build().property("streetAndNumber").getField());
    }

}
