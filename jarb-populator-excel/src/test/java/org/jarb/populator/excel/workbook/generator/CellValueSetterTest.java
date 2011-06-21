//package org.jarb.populator.excel.workbook.generator;
//
//import static org.junit.Assert.assertEquals;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.util.Date;
//
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CreationHelper;
//import org.junit.Before;
//import org.junit.Test;
//
//public class CellValueSetterTest {
//
//    @Before
//    public void setupCellValueSetterTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
//            IllegalAccessException, InvocationTargetException {
//        //For code coverage purposes:
//        Constructor<CellValueSetter> constructor = CellValueSetter.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        constructor.newInstance();
//    }
//
//    @Test
//    public void testSetCellValueByProperTypeDate() {
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet workpage = workbook.createSheet("Test");
//        HSSFRow row = workpage.createRow(0);
//        Date date = new Date();
//        date.setTime(1256953732);
//
//        CreationHelper createHelper = workbook.getCreationHelper();
//        CellStyle dateFormatStyle = workbook.createCellStyle();
//        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));
//        dateFormatStyle.setAlignment(CellStyle.ALIGN_LEFT);
//
//        int columnNumber = 1;
//
//        CellValueSetter.setCellValueByProperType(row, columnNumber, date, dateFormatStyle);
//        assertEquals("Thu Jan 15 14:09:13 CET 1970", row.getCell(1).getDateCellValue().toString());
//    }
//
//    @Test
//    public void testSetCellValueByProperTypeInvalidObject() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
//            InvocationTargetException {
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet workpage = workbook.createSheet("Test");
//        HSSFRow row = workpage.createRow(0);
//        Object invalidObject = new HSSFWorkbook();
//
//        CellValueSetter.setCellValueByProperType(row, 1, invalidObject, null);
//        assertEquals(null, row.getCell(1));
//    }
//}
