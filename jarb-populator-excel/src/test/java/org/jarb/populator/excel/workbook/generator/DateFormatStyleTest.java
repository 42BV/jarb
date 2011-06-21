//package org.jarb.populator.excel.workbook.generator;
//
//import static org.junit.Assert.assertEquals;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.junit.Before;
//import org.junit.Test;
//
//public class DateFormatStyleTest {
//
//    @Before
//    public void setupDateFormatStyleTest() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
//            SecurityException, NoSuchMethodException {
//        //For code coverage purposes:
//        Constructor<DateFormatStyle> constructor = DateFormatStyle.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        constructor.newInstance();
//    }
//
//    @Test
//    public void testGetDateFormatStyle() {
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        CellStyle cellStyle = DateFormatStyle.getDateFormatStyle(workbook);
//        assertEquals(CellStyle.ALIGN_LEFT, cellStyle.getAlignment());
//        assertEquals("yyyy/mm/dd", cellStyle.getDataFormatString());
//    }
//
//}
