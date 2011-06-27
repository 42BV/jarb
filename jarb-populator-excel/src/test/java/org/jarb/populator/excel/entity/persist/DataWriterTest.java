package org.jarb.populator.excel.entity.persist;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.mapping.importer.ExcelImporter;
import org.jarb.populator.excel.mapping.importer.ExcelRow;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyVehicle;
import domain.entities.Customer;
import domain.entities.Employee;
import domain.entities.Project;
import domain.entities.ServiceLevelAgreement;

public class DataWriterTest extends DefaultExcelTestDataCase {
    private Map<EntityDefinition<?>, Map<Object, ExcelRow>> parseExcelMap;
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

        customer = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), customerEntity, false);
        project = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), projectEntity, false);
        sla = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), slaEntity, false);

        classDefinitionList.add(customer);
        classDefinitionList.add(project);
        classDefinitionList.add(sla);

        parseExcelMap = ExcelImporter.parseExcel(excel, classDefinitionList);
        DataWriter.saveEntity(toRegistry(parseExcelMap), getEntityManagerFactory());
    }

    @Test
    public void testEntityReferencing() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException,
            InvalidFormatException, IOException {
        List<EntityDefinition<?>> classDefinitionList = new ArrayList<EntityDefinition<?>>();
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelEmployeesVehicles.xls"));

        EntityType<?> employeeEntity = jpaMetamodel.entity(Employee.class);
        EntityType<?> vehicleEntity = jpaMetamodel.entity(CompanyVehicle.class);

        EntityDefinition<?> employee = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), employeeEntity, false);
        EntityDefinition<?> vehicle = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), vehicleEntity, true);
        //  project = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, projectEntity, true);

        classDefinitionList.add(employee);
        classDefinitionList.add(vehicle);
        //  classDefinitionList.add(project);

        parseExcelMap = ExcelImporter.parseExcel(excel, classDefinitionList);
        DataWriter.saveEntity(toRegistry(parseExcelMap), getEntityManagerFactory());
    }
    
    @SuppressWarnings("unchecked")
    private EntityRegistry toRegistry(Map<EntityDefinition<?>, Map<Object, ExcelRow>> entitiesMap) {
        EntityRegistry registry = new EntityRegistry();
        for (Map.Entry<EntityDefinition<?>, Map<Object, ExcelRow>> entitiesEntry : entitiesMap.entrySet()) {
            @SuppressWarnings("rawtypes")
            final Class entityClass = entitiesEntry.getKey().getEntityClass();
            EntityTable<Object> table = new EntityTable<Object>(entityClass);
            for (Map.Entry<Object, ExcelRow> excelRowEntry : entitiesEntry.getValue().entrySet()) {
                table.add(excelRowEntry.getKey(), excelRowEntry.getValue().getCreatedInstance());
            }
            registry.addAll(table);
        }
        return registry;
    }

//    private MetaModel metamodel;
//    
//    @Before
//    public void setUpDatabaseConnectionTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, SecurityException,
//            NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
//        metamodel = generateMetamodel();
//        
//        // For code coverage purposes:
//        Constructor<DataWriter> constructor = DataWriter.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        constructor.newInstance();
//    }
//
//    @Test
//    public void testSaveEntity() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, ClassNotFoundException,
//            InvalidFormatException, IOException {
//        Workbook excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
//
//        List<EntityDefinition<?>> classDefinitionList = new ArrayList<EntityDefinition<?>>();
//        classDefinitionList.add(metamodel.entity(Customer.class));
//        classDefinitionList.add(metamodel.entity(Project.class));
//        classDefinitionList.add(metamodel.entity(ServiceLevelAgreement.class));
//
//        Map<EntityDefinition<?>, Map<Object, ExcelRow>> parseExcelMap = ExcelImporter.parseExcel(excel, classDefinitionList);
//        DataWriter.saveEntity(toRegistry(parseExcelMap), getEntityManagerFactory());
//    }
//
//    @Test
//    public void testEntityReferencing() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException,
//            InvalidFormatException, IOException {
//        Workbook excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelEmployeesVehicles.xls"));
//
//        List<EntityDefinition<?>> classDefinitionList = new ArrayList<EntityDefinition<?>>();
//        classDefinitionList.add(metamodel.entity(Employee.class));
//        classDefinitionList.add(metamodel.entity(CompanyVehicle.class));
//        
//        Map<EntityDefinition<?>, Map<Object, ExcelRow>> parseExcelMap = ExcelImporter.parseExcel(excel, classDefinitionList);
//        DataWriter.saveEntity(toRegistry(parseExcelMap), getEntityManagerFactory());
//    }

}
