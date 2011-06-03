package org.jarb.populator.excel.workbook.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Checks a set of columnNames delivered from ExcelFileValidator for equality with the column names in the Excel file.
 * If this is not equal, further analysis is performed to determine the differences
 * Bundles all the messages in a set and returns this
 * @author Sander Benschop
 *
 */
public final class ColumnValidator {

    private ColumnValidator() {
    }

    /**
     * Checks if a specified Excel table holds all columns presented in the mapping and vice versa.
     * @param excel representation of excel file
     * @param columnNames Set of columnNames from the mapping
     * @param tableName Name of the Excel sheet
     * @return List of verification outcomes, these can either be error messages or messages that everything is fine
     */
    public static List<String> validateColumnsInSheet(Workbook excel, Set<String> columnNames, String tableName) {
        List<String> returnValues = new ArrayList<String>();
        Sheet sheet = excel.getSheet(tableName);
        if (sheet != null) {
            Set<String> excelColumnNames = sheet.getColumnNames();
            if (columnNames.equals(excelColumnNames)) {
                // Sets are equal, no further comparison is neccessary.
                returnValues.add("Excel columns in worksheet [" + tableName + "] match the mapping.");
            } else {
                returnValues.addAll(findDifferencesBetweenColumnSets(excelColumnNames, columnNames, tableName));
            }
        }
        return returnValues;
    }

    /**
     * Finds differences between columns from the mapping and columns from an Excel file.
     * @param excelColumnNames Column names from the Excel file
     * @param columnNames Column names from the mapping
     * @param tableName Name of the table
     * @return List of messages
     */
    public static List<String> findDifferencesBetweenColumnSets(Set<String> excelColumnNames, Set<String> columnNames, String tableName) {
        List<String> returnValues = new ArrayList<String>();
        returnValues.addAll(compareExcelColumnNamestoMapping(excelColumnNames, columnNames, tableName));
        returnValues.addAll(compareMappingToExcelColumnNames(excelColumnNames, columnNames, tableName));
        return returnValues;
    }

    /**
     * Compares the column names of the Excel file to the ones in the mapping.
     * @param excelColumnNames Column names from the Excel file
     * @param columnNames Column names from the mapping
     * @param tableName Name of the table
     * @return List of messages
     */
    private static List<String> compareMappingToExcelColumnNames(Set<String> excelColumnNames, Set<String> columnNames, String tableName) {
        List<String> returnValues = new ArrayList<String>();
        for (String excelColumnName : excelColumnNames) {
            if (!columnNames.contains(excelColumnName)) {
                returnValues.add("Error in Excel worksheet [" + tableName + "] : Mapping does not contain column [" + excelColumnName
                        + "] present in the Excel sheet.");
            }
        }
        return returnValues;
    }

    /**
     * Compares the column names of the mapping to the ones in the Excel file.
     * @param excelColumnNames Column names from the Excel file
     * @param columnNames Column names from the mapping
     * @param tableName Name of the table
     * @return List of messages
     */
    private static List<String> compareExcelColumnNamestoMapping(Set<String> excelColumnNames, Set<String> columnNames, String tableName) {
        List<String> returnValues = new ArrayList<String>();
        for (String columnName : columnNames) {
            if (!excelColumnNames.contains(columnName)) {
                returnValues.add("Error in Excel worksheet [" + tableName + "]: Sheet does not contain column [" + columnName + "] present in the mapping.");
            }
        }
        return returnValues;
    }

}
