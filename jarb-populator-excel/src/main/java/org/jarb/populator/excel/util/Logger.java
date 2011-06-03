package org.jarb.populator.excel.util;

import java.util.Map;
import java.util.Map.Entry;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.ClassDefinition;

/**
 * Logs all important activity to the console.
 * Not yet finished.
 * @author Sander Benschop
 *
 */
public class Logger {

    /**
     * Logs table names, row keys and values to console.
     * @param objectModel Map with ClassDefinitions, Keys and ExcelRecords.
     */
    public void reportExportedInstances(final Map<ClassDefinition, Map<Integer, ExcelRow>> objectModel) {
        for (Entry<ClassDefinition, Map<Integer, ExcelRow>> tableRecords : objectModel.entrySet()) {
            printTableRecords(tableRecords);
        }
    }

    /**
     * Logs table records.
     * @param tableRecords Map with Keys and ExcelRecords
     */
    private void printTableRecords(Entry<ClassDefinition, Map<Integer, ExcelRow>> tableRecords) {
        System.out.println(tableRecords.getKey().getTableName());
        for (Entry<Integer, ExcelRow> rowRecords : tableRecords.getValue().entrySet()) {
            System.out.println(rowRecords.getKey() + ": " + tableRecords.getValue().get(rowRecords.getKey()).toString());
        }
    }
}
