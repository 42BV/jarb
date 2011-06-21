//package org.jarb.populator.excel.workbook.generator;
//
//import java.util.Date;
//
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Sets a certain value to an Excel cell. The way it saves this depends on the object's type.
// * @author Sander Benschop
// *
// */
//public final class CellValueSetter {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CellValueSetter.class);
//
//    /** Private constructor. */
//    private CellValueSetter() {
//    }
//
//    /**
//     * Creates a new Excel file based on the type of the value.
//     * @param row An excel row to add cells to
//     * @param columnNumber The index of the column that is being saved (0-based)
//     * @param cellValue The Cell value which is to be saved
//     * @param dateFormatStyle The date format style needed when saving dates to the Excel file
//     */
//    protected static void setCellValueByProperType(HSSFRow row, int columnNumber, Object cellValue, CellStyle dateFormatStyle) {
//        Class<?> type = cellValue.getClass();
//
//        if ((type == java.lang.Long.class)) {
//            setLongCellValue(row, columnNumber, cellValue);
//        } else if (type == java.lang.Double.class) {
//            setDoubleCellValue(row, columnNumber, cellValue);
//        } else if (type == java.lang.Integer.class) {
//            setIntegerCellValue(row, columnNumber, cellValue);
//        } else if (type == java.lang.Boolean.class) {
//            setBooleanCellValue(row, columnNumber, cellValue);
//        } else if (type == java.lang.String.class) {
//            setStringCellValue(row, columnNumber, cellValue);
//        } else if (type == java.lang.Character.class) {
//            setCharacterCellValue(row, columnNumber, cellValue);
//        } else if ((type == java.util.Date.class) || (type == java.sql.Timestamp.class) || (type == java.sql.Date.class)) {
//            setDateCellValue(row, columnNumber, cellValue, dateFormatStyle);
//        } else {
//            LOGGER.warn("Object " + cellValue + " is not of a suitable type to be saved to an Excel file.");
//        }
//    }
//
//    /**
//     * Creates a new cell with a Long cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue Long cell value
//     */
//    private static void setLongCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue(Long.valueOf(((Long) cellValue)));
//    }
//
//    /**
//     * Creates a new cell with a Double cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue Double cell value
//     */
//    private static void setDoubleCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue((Double) cellValue);
//    }
//
//    /**
//     * Creates a new cell with a Integer cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue Integer cell value
//     */
//    private static void setIntegerCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue((Integer) cellValue);
//    }
//
//    /**
//     * Creates a new cell with a Boolean cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue Boolean cell value
//     */
//    private static void setBooleanCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue((Boolean) cellValue);
//    }
//
//    /**
//     * Creates a new cell with a String cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue String cell value
//     */
//    private static void setStringCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue((String) cellValue);
//    }
//
//    /**
//     * Creates a new cell with a String cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue String cell value
//     */
//    private static void setCharacterCellValue(HSSFRow row, int columnNumber, Object cellValue) {
//        row.createCell(columnNumber).setCellValue(cellValue.toString());
//    }
//
//    /**
//     * Creates a new cell with a Date cell value.
//     * @param row Current row
//     * @param columnNumber Current column number
//     * @param cellValue String cell value
//     * @param dateFormatStlye The preferred date format style, we use the database conventional yyyy-mm-dd
//     */
//    private static void setDateCellValue(HSSFRow row, int columnNumber, Object cellValue, CellStyle dateFormatStyle) {
//        Date date = (Date) cellValue;
//        row.createCell(columnNumber).setCellValue(date);
//        row.getCell(columnNumber).setCellStyle(dateFormatStyle);
//    }
//}
