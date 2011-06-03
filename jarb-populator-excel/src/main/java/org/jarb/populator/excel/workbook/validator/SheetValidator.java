package org.jarb.populator.excel.workbook.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jarb.populator.excel.workbook.Workbook;

/**
 * Checks if the exact sheets are available in the Excel file as in the mapping.
 * If this is not equal, further analysis is performed to determine the differences
 * Bundles all the messages in a set and returns this.
 * @author Sander Benschop
 *
 */
public final class SheetValidator {

    private SheetValidator() {
    }

    /**
     * Checks if all class mappings are available in the Excel sheet and vice versa.
     * @param excel representation of excel file
     * @param mappingSheetNames Set of sheets generated from the mapping
     * @return List of verification outcomes, these can either be error messages or messages that everything is fine
     */
    public static List<String> checkSheetAvailability(Workbook excel, Set<String> mappingSheetNames) {
        List<String> returnValues = new ArrayList<String>();
        Set<String> excelSheetNames = new HashSet<String>(excel.getSheetNames());

        if (mappingSheetNames.equals(excelSheetNames)) {
            //Sheets are equal, no further analysis is neccessary.
            returnValues.add("All the sheets specified in the mapping are present in the Excel file.");
        } else {
            returnValues.addAll(findDifferencesBetweenSheetSets(excelSheetNames, mappingSheetNames));
        }
        return returnValues;
    }

    /**
     * Finds differences between sheets from the mapping and sheets from an Excel file.
     * @param excelSheetNames Sheetnames from the Excel file
     * @param mappingSheetNames Sheetnames from the mapping
     * @return List of messages
     */
    public static List<String> findDifferencesBetweenSheetSets(Set<String> excelSheetNames, Set<String> mappingSheetNames) {
        List<String> returnValues = new ArrayList<String>();
        returnValues.addAll(loopOverMappingSheetsToFindDifferences(excelSheetNames, mappingSheetNames));
        returnValues.addAll(loopOverExcelSheetsToFindDifferences(excelSheetNames, mappingSheetNames));
        return returnValues;
    }

    /**
     * Loops over all the mapping's sheets and checks if these are available in the mapping to find possible differences.
     * @param excelSheetNames Set of excel sheet names
     * @param mappingSheetNames Set of mapping sheet names
     * @param returnValues List of messages
     */
    private static List<String> loopOverMappingSheetsToFindDifferences(Set<String> excelSheetNames, Set<String> mappingSheetNames) {
        List<String> returnValues = new ArrayList<String>();
        for (String sheets : mappingSheetNames) {
            if (!excelSheetNames.contains(sheets)) {
                returnValues.add("Error in Excel file. Worksheet [" + sheets + "] from the mapping is not present in the Excel file.");
            }
        }
        return returnValues;
    }

    /**
     * Loops over all the Excel sheets and checks if these are available in the Excel file to find possible differences.
     * @param excelSheetNames Set of excel sheet names
     * @param mappingSheetNames Set of mapping sheet names
     * @param returnValues List of messages
     */
    private static List<String> loopOverExcelSheetsToFindDifferences(Set<String> excelSheetNames, Set<String> mappingSheetNames) {
        List<String> returnValues = new ArrayList<String>();
        for (String excelSheet : excelSheetNames) {
            if (!mappingSheetNames.contains(excelSheet)) {
                returnValues.add("Error in Excel file. Worksheet [" + excelSheet + "] is not present in the mapping.");
            }
        }
        return returnValues;
    }

}
