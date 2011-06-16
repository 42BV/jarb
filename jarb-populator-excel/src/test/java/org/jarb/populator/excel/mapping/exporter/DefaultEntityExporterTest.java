package org.jarb.populator.excel.mapping.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;

public class DefaultEntityExporterTest extends DefaultExcelTestDataCase {
    private DefaultEntityExporter exporter;
    private EntityRegistry registry;
    private MetaModel metamodel;

    @Before
    public void setUpExporter() {
        exporter = new DefaultEntityExporter();
        registry = new EntityRegistry();
        metamodel = getExcelDataManagerFactory().buildMetamodelGenerator().generateFor(CompanyVehicle.class);
    }

    @Test
    public void testColumns() {
        Workbook workbook = exporter.export(registry, metamodel);
        assertEquals(1, workbook.getSheetCount());
        assertTrue(workbook.containsSheet("vehicles"));
        Sheet vehiclesSheet = workbook.getSheet("vehicles");
        ClassDefinition<CompanyVehicle> vehiclesDefinition = metamodel.getClassDefinition(CompanyVehicle.class);
        // Each column should be stored inside the workbook
        for(String columnName : vehiclesDefinition.getColumnNames()) {
            assertTrue(vehiclesSheet.containsColumn(columnName));
        }
    }
    
    @Test
    public void testDiscriminator() {
        CompanyCar car = new CompanyCar("bugatti", 999999D, 0, Gearbox.MANUAL, true);
        registry.add(CompanyVehicle.class, 1L, car);
        Workbook workbook = exporter.export(registry, metamodel);
        Sheet vehiclesSheet = workbook.getSheet("vehicles");
        ClassDefinition<CompanyVehicle> vehiclesDefinition = metamodel.getClassDefinition(CompanyVehicle.class);
        String discriminatorColumn = vehiclesDefinition.getDiscriminatorColumnName();
        String carDiscriminatorValue = vehiclesDefinition.getDiscriminatorValue(CompanyCar.class);
        assertEquals(carDiscriminatorValue, vehiclesSheet.getCellValueAt(1, discriminatorColumn));
    }

}
