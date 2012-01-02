package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.Department;
import domain.entities.Employee;
import domain.entities.Phone;

public class ColumnDefinitionsGeneratorTest extends DefaultExcelTestDataCase {
    
    private ColumnDefinitionsGenerator columnDefinitionsGenerator;

    private Metamodel metamodel;
    
    @Before
    public void setUp() {
        columnDefinitionsGenerator = new ColumnDefinitionsGenerator(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
        metamodel = getEntityManagerFactory().getMetamodel();
    }

    @Test
    public void testCreateColumnDefinitions() throws NoSuchFieldException {
        Class<Department> persistentClass = Department.class;

        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();

        EntityDefinition.Builder<Department> classDefinitionBuilder = EntityDefinition.forClass(Department.class).setTableName("departments");

        classDefinitionBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(persistentClass.getDeclaredField("departmentName"), classDefinitionBuilder.build().property("departmentName").getField());
    }

    @Test
    public void testCreateColumnDefinitionsWithEmbeddables() throws NoSuchFieldException {
        Class<Employee> persistentClass = Employee.class;

        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();

        EntityDefinition.Builder<Employee> classDefinitionBuilder = EntityDefinition.forClass(Employee.class).setTableName("employees");
        classDefinitionBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(Address.class.getDeclaredField("streetAndNumber"), classDefinitionBuilder.build().property("streetAndNumber").getField());
    }
    
    @Test
    public void testCreatePropertyDefinitionsGeneratorForEmbeddableElementCollection(){
        Class<Phone> phone = Phone.class;
        EmbeddableType<?> embeddable = metamodel.embeddable(phone);
        EmbeddableElementCollectionDefinition.Builder<Phone> phoneDefinitionBuilder = EmbeddableElementCollectionDefinition.forClass(Phone.class);
        phoneDefinitionBuilder.includeProperties(columnDefinitionsGenerator.createPropertyDefinitions(embeddable, phone));
        EmbeddableElementCollectionDefinition<Phone> phoneDefinition = phoneDefinitionBuilder.build();
        assertEquals(2, phoneDefinition.properties().size());
        assertEquals(Phone.class, phoneDefinition.getDefinedClass());
    }

}
