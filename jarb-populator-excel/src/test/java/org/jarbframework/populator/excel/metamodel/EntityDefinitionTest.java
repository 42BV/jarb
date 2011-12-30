package org.jarbframework.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.generator.ColumnDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.SubclassRetriever;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.Employee;

public class EntityDefinitionTest extends DefaultExcelTestDataCase {

    private EntityDefinition<Employee> employeeDefinition;
    private EntityDefinition<CompanyVehicle> vehicleDefinition;
    private Class<Employee> employee;
    private Class<CompanyVehicle> vehicle;

    @Before
    public void prepareTest() {
        employee = Employee.class;
        EntityType<Employee> departmentEntity = getEntityManagerFactory().getMetamodel().entity(employee);

        SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory());
        ColumnDefinitionsGenerator columnDefinitionsGenerator = new ColumnDefinitionsGenerator(schemaMapper);

        EntityDefinition.Builder<Employee> employeeBuilder = EntityDefinition.forClass(employee);
        employeeBuilder.setTableName("departments");
        employeeBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(new HashSet<EntityType<?>>(), departmentEntity, employee));
        employeeDefinition = employeeBuilder.build();

        vehicle = CompanyVehicle.class;
        EntityType<CompanyVehicle> vehicleEntity = getEntityManagerFactory().getMetamodel().entity(vehicle);
        Set<EntityType<?>> vehicleSubclasses = SubclassRetriever.getSubClassEntities(vehicleEntity, getEntityManagerFactory().getMetamodel().getEntities());
        EntityDefinition.Builder<CompanyVehicle> vehicleBuilder = EntityDefinition.forClass(vehicle);
        vehicleBuilder.setTableName("vehicles");
        vehicleBuilder.includeSubClass("car", CompanyCar.class);
        vehicleBuilder.setDiscriminatorColumnName("type");
        vehicleBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(vehicleSubclasses, vehicleEntity, vehicle));
        vehicleDefinition = vehicleBuilder.build();
    }

    @Test
    public void testProperties() {
        assertEquals(10, employeeDefinition.propertyDefinitions.size());
    }

    @Test
    public void testProperty() throws SecurityException, NoSuchFieldException {
        PropertyDefinition name = employeeDefinition.property("name");
        assertEquals(employee.getDeclaredField("name"), name.getField());
    }

    @Test
    public void testPropertyByColumnName() throws SecurityException, NoSuchFieldException {
        PropertyDefinition name = employeeDefinition.propertyByColumnName("first_name");
        assertEquals(employee.getDeclaredField("name"), name.getField());
    }

    @Test
    public void testFieldNameByColumnName() {
        assertEquals("name", employeeDefinition.fieldNameByColumnName("first_name"));
    }

    @Test
    public void testGetColumnNames() {
        assertEquals(8, employeeDefinition.getColumnNames().size());
    }

    @Test
    public void testGetEntitySubclass() {
        assertEquals(CompanyCar.class, vehicleDefinition.getEntitySubClass("car"));
    }

    @Test
    public void testGetDiscriminatorValue() {
        assertEquals("car", vehicleDefinition.getDiscriminatorValue(CompanyCar.class));
    }

    @Test
    public void testGetDiscriminatorColumnName() {
        assertEquals("type", vehicleDefinition.getDiscriminatorColumnName());
    }

    @Test
    public void testHasDiscriminatorColumn() {
        assertTrue(vehicleDefinition.hasDiscriminatorColumn());
    }

    @Test
    public void testGetColumnNamesWithDiscriminator() {
        assertTrue(vehicleDefinition.getColumnNames().contains("type"));
    }

    @Test
    public void testGetIdColumnName() {
        assertEquals("employee_id", employeeDefinition.getIdColumnName());
    }
    
    @Test
    public void testBuildDefinitionOfTypeENTITY(){
        
    }
    
    @Test
    public void testBuildDefinitionOfTypeEMBEDDABLE_ELEMENTCOLLECTION(){
        
    }   

    @Test
    public void testBuildDefinitionOfTypeSIMPLE_DATATYPE_ELEMENTCOLLECTION(){
        
    }   
    
}
