package org.jarb.populator.excel.mapping.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Release;

public class DefaultEntityExporterTest extends DefaultExcelTestDataCase {
    private DefaultEntityExporter entityExporter;
    private MetaModel metamodel;

    @Before
    public void setUpExporter() {
        entityExporter = new DefaultEntityExporter();
        metamodel = getExcelDataManagerFactory().buildMetamodelGenerator().generateFor(Release.class);
    }

    @Test
    public void testExport() {
        EntityRegistry registry = new EntityRegistry();
        Workbook workbook = entityExporter.export(registry, metamodel);
        assertEquals(1, workbook.getSheetCount());
        assertTrue(workbook.containsSheet("releases"));
    }

}
