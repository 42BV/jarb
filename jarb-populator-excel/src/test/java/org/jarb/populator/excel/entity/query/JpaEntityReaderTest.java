package org.jarb.populator.excel.entity.query;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.entity.EntityRegistry;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Project;

public class JpaEntityReaderTest extends DefaultExcelTestDataCase {
    private JpaEntityReader entityReader;

    @Before
    public void setUpReader() {
        entityReader = new JpaEntityReader(getEntityManagerFactory());
    }

    @Test
    public void testFetchAll() throws FileNotFoundException {
        getExcelDataManager().persistWorkbook("src/test/resources/Excel.xls");
        EntityRegistry registry = entityReader.fetchAll();
        assertFalse(registry.getAll(Project.class).isEmpty());
    }

}
