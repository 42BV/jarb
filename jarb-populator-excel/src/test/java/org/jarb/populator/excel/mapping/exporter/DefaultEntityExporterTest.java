package org.jarb.populator.excel.mapping.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.BusinessRelationshipGift;
import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;
import domain.entities.Customer;
import domain.entities.Employee;
import domain.entities.VeryImportantCustomer;

public class DefaultEntityExporterTest extends DefaultExcelTestDataCase {
    private DefaultEntityExporter exporter;
    private EntityRegistry registry;
    
    private MetaModelGenerator metamodelGenerator;
    private MetaModel metamodel;

    @Before
    public void setUpExporter() {
        exporter = new DefaultEntityExporter(new ValueConversionService());
        registry = new EntityRegistry();
        metamodelGenerator = getExcelDataManagerFactory().buildMetamodelGenerator();
        metamodel = metamodelGenerator.generate();
    }

    @Test
    public void testSheetAndColumnsCreated() {
        MetaModel metamodel = metamodelGenerator.generate();
        Workbook workbook = exporter.export(registry, metamodel);
        assertTrue(workbook.containsSheet("vehicles"));
        Sheet vehiclesSheet = workbook.getSheet("vehicles");
        EntityDefinition<? super CompanyVehicle> vehiclesDefinition = metamodel.entity(CompanyVehicle.class);
        // Each column should be stored inside the workbook
        for(String columnName : vehiclesDefinition.getColumnNames()) {
            assertTrue(vehiclesSheet.containsColumn(columnName));
        }
    }
    
    @Test
    public void testDiscriminatorCustomColumn() {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 0, Gearbox.MANUAL, true);
        registry.add(CompanyVehicle.class, 1L, car);
        Workbook workbook = exporter.export(registry, metamodel);
        Sheet vehiclesSheet = workbook.getSheet("vehicles");
        EntityDefinition<? super CompanyVehicle> vehiclesDefinition = metamodel.entity(CompanyVehicle.class);
        String carDiscriminatorValue = vehiclesDefinition.getDiscriminatorValue(CompanyCar.class);
        assertEquals(carDiscriminatorValue, vehiclesSheet.getValueAt(1, "type"));
    }
    
    @Test
    public void testValueColumns() {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        registry.add(CompanyVehicle.class, 1L, car);
        Workbook workbook = exporter.export(registry, metamodel);
        Sheet vehiclesSheet = workbook.getSheet("vehicles");
        assertEquals("bugatti", vehiclesSheet.getValueAt(1, "model"));
        assertEquals(Double.valueOf(999999), vehiclesSheet.getValueAt(1, "price"));
        assertEquals(Double.valueOf(42), vehiclesSheet.getValueAt(1, "mileage"));
        assertEquals("MANUAL", vehiclesSheet.getValueAt(1, "gearbox"));
        assertEquals(Boolean.TRUE, vehiclesSheet.getValueAt(1, "airbags"));
    }
    
    @Test
    public void testJoinColumn() {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 42, Gearbox.MANUAL, true);
        registry.add(CompanyVehicle.class, 42L, car);
        Employee employee = new Employee();
        employee.setVehicle(car);
        registry.add(Employee.class, 24L, employee);
        Workbook workbook = exporter.export(registry, metamodel);
        Object carIdentifier = workbook.getSheet("vehicles").getValueAt(1, 0);
        assertEquals(carIdentifier, workbook.getSheet("employees").getValueAt(1, "company_vehicle_id"));
    }
    
    @Test
    public void testJoinTable() {
        BusinessRelationshipGift gift = new BusinessRelationshipGift();
        registry.add(BusinessRelationshipGift.class, 1L, gift);
        BusinessRelationshipGift anotherGift = new BusinessRelationshipGift();
        registry.add(BusinessRelationshipGift.class, 2L, anotherGift);
        VeryImportantCustomer customer = new VeryImportantCustomer();
        customer.addGift(gift);
        customer.addGift(anotherGift);
        registry.add(Customer.class, 1L, customer);
        Workbook workbook = exporter.export(registry, metamodel);
        Sheet joinSheet = workbook.getSheet("vipcustomers_gifts");
        assertNotNull("Join sheet was not created", joinSheet);
        assertEquals("customer_id", joinSheet.getValueAt(0, 0));
        assertEquals("gift_id", joinSheet.getValueAt(0, 1));
        Object customerId = workbook.getSheet("customers").getValueAt(1, 0);
        assertEquals(customerId, joinSheet.getValueAt(1, "customer_id"));
        assertEquals(workbook.getSheet("gifts").getValueAt(1, 0), joinSheet.getValueAt(1, "gift_id"));
        assertEquals(customerId, joinSheet.getValueAt(2, "customer_id"));
        assertEquals(workbook.getSheet("gifts").getValueAt(2, 0), joinSheet.getValueAt(2, "gift_id"));
    }
    
    @Test
    public void testEmbeddable() {
        Employee employee = new Employee();
        Address address = new Address("Test street 42", "Amsterdam");
        employee.setAddress(address);
        registry.add(Employee.class, 1L, employee);
        Workbook workbook = exporter.export(registry, metamodel);
        Sheet employeesSheet = workbook.getSheet("employees");
        assertEquals("Test street 42", employeesSheet.getValueAt(1, "building_address"));
        assertEquals("Amsterdam", employeesSheet.getValueAt(1, "city_name"));
    }

}
