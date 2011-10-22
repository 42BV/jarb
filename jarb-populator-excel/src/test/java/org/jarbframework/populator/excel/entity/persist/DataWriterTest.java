package org.jarbframework.populator.excel.entity.persist;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.mapping.importer.ExcelImporter;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyVehicle;
import domain.entities.Customer;
import domain.entities.Employee;
import domain.entities.Project;
import domain.entities.ServiceLevelAgreement;

public class DataWriterTest extends DefaultExcelTestDataCase {
    private ClassDefinitionsGenerator classDefinitionsGenerator;
    private Workbook excel;
    private EntityDefinition<?> customer;
    private EntityDefinition<?> project;
    private EntityDefinition<?> sla;
    private Metamodel jpaMetamodel;
    private EntityType<?> customerEntity;
    private EntityType<?> projectEntity;
    private EntityType<?> slaEntity;

    @Before
    public void setUpDatabaseConnectionTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        jpaMetamodel = getEntityManagerFactory().getMetamodel();
        customerEntity = jpaMetamodel.entity(Customer.class);
        projectEntity = jpaMetamodel.entity(Project.class);
        slaEntity = jpaMetamodel.entity(ServiceLevelAgreement.class);

        // For code coverage purposes:
        Constructor<DataWriter> constructor = DataWriter.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testSaveEntity() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, ClassNotFoundException,
            InvalidFormatException, IOException {
        List<EntityDefinition<?>> classDefinitionList = new ArrayList<EntityDefinition<?>>();

        customer = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(customerEntity, false);
        project = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(projectEntity, false);
        sla = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(slaEntity, false);

        classDefinitionList.add(customer);
        classDefinitionList.add(project);
        classDefinitionList.add(sla);

        ExcelImporter excelImporter = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory());
        EntityRegistry entityRegistry = excelImporter.parseExcelToRegistry(excel, classDefinitionList);
        DataWriter.saveEntity(entityRegistry, getEntityManagerFactory());
    }

    @Test
    public void testEntityReferencing() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException,
            InvalidFormatException, IOException {
        List<EntityDefinition<?>> classDefinitionList = new ArrayList<EntityDefinition<?>>();
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelEmployeesVehicles.xls"));

        EntityType<?> employeeEntity = jpaMetamodel.entity(Employee.class);
        EntityType<?> vehicleEntity = jpaMetamodel.entity(CompanyVehicle.class);

        EntityDefinition<?> employee = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(employeeEntity, false);
        EntityDefinition<?> vehicle = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(vehicleEntity, true);
        //  project = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, projectEntity, true);

        classDefinitionList.add(employee);
        classDefinitionList.add(vehicle);
        //  classDefinitionList.add(project);

        EntityRegistry entityRegistry = new ExcelImporter(ValueConversionService.defaultConversions(), getEntityManagerFactory()).parseExcelToRegistry(excel,
                classDefinitionList);
        DataWriter.saveEntity(entityRegistry, getEntityManagerFactory());
    }
}
