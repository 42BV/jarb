package org.jarb.populator.excel.workbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class WorkbookTest {
    private Workbook excel;

    @Before
    public void setUp() {
        excel = new Workbook();
    }

    @Test
    public void testAddAndReplaceSheet() {
        assertEquals(0, excel.getSheetCount());
        final Sheet sheet = new Sheet("test");
        excel.addSheet(sheet);
        assertEquals(1, excel.getSheetCount());
        assertEquals(sheet, excel.getSheetAt(0));
        List<Sheet> sheets = excel.getSheets();
        assertEquals(Arrays.asList(sheet), sheets);
        final Sheet another = new Sheet("another");
        excel.setSheet(0, another);
        assertEquals(another, excel.getSheetAt(0));
        assertEquals(1, excel.getSheetCount());
        excel.removeSheet(another);
        assertEquals(0, excel.getSheetCount());
    }

    @Test
    public void testFindByName() {
        final Sheet sheet = new Sheet("test");
        final Sheet another = new Sheet("another");
        excel.addSheet(sheet).addSheet(another);
        assertEquals(sheet, excel.getSheet("test"));
        assertEquals(another, excel.getSheet("another"));
        assertNull(excel.getSheet("unknown"));
    }

}
