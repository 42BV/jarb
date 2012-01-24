package org.jarbframework.populator.excel.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ColumnDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.JpaMetaModelGenerator;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class JpaUtilsTest extends DefaultExcelTestDataCase {

    private EntityDefinition<Employee> employeeDefinition;
    private Metamodel metamodel;

    @Before
    public void setupJpaUtilsTest() {
        Class<Employee> employee = Employee.class;
        EntityType<Employee> employeeEntity = getEntityManagerFactory().getMetamodel().entity(employee);
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(getEntityManagerFactory());

        EntityDefinition.Builder<Employee> employeeBuilder = EntityDefinition.forClass(employee);
        employeeBuilder.setTableName("employees");
        employeeBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(employeeEntity, employee));
        employeeDefinition = employeeBuilder.build();

        metamodel = getEntityManagerFactory().getMetamodel();
    }

    @Test
    public void testCreateEntityManager() {
        EntityManager em = JpaUtils.createEntityManager(getEntityManagerFactory());
        assertNotNull(em);
        assertNotNull(em.getProperties());
    }

    @Test
    public void testGetJoinColumnNamesFromElementCollectionFieldWithCollectionTable() {
        HashMap<String, String> joinColumnNames = new HashMap<String, String>();
        joinColumnNames.put("#", "owner");
        PropertyDefinition phones = employeeDefinition.property("phones");
        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory());
        assertEquals(joinColumnNames, JpaUtils.getJoinColumnNamesFromJpaAnnotatedField(schemaMapper, metamodel.entity(Employee.class), phones.getField()));
    }

    @Test
    public void testGetJoinColumnNamesFromElementCollectionFieldWithoutCollectionTable() {
        HashMap<String, String> joinColumnNames = new HashMap<String, String>();
        joinColumnNames.put("#", "employees_employee_id");
        PropertyDefinition emailAdresses = employeeDefinition.property("emailAddresses");
        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory());
        assertEquals(joinColumnNames,
                JpaUtils.getJoinColumnNamesFromJpaAnnotatedField(schemaMapper, metamodel.entity(Employee.class), emailAdresses.getField()));
    }

    @Test
    public void testGetElementCollectionColumnNames() {
        JpaMetaModelGenerator jpaMetaModelGenerator = new JpaMetaModelGenerator(getEntityManagerFactory());
        MetaModel metaModel = jpaMetaModelGenerator.generate();

        Set<String> columnNames = new HashSet<String>();
        columnNames.add("phone_model");
        columnNames.add("phone_number");

        assertEquals(columnNames, JpaUtils.getElementCollectionColumnNames(employeeDefinition.property("phones"), metaModel));
    }
}
