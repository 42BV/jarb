package org.jarb.populator.excel.workbook.generator;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.JoinTable;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Has basic functions to create Excel sheets used by both the FilledExcelFileGenerator and the NewExcelFileGenerator.
 * Can make both regular or associative tables.
 * Also holds functionality to write the Excel file.
 * @author Sander Benschop
 *
 */
public final class BasicExcelFileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicExcelFileGenerator.class);

    /** Identification number of row in Excel file. */
    private static final String IDCOLUMNNAME = "id";

    /** Private constructor. */
    private BasicExcelFileGenerator() {
    }

    /**
     * Creates an excel table, adds id as primary key to table and calls createJoinTable is a columnDefinition is going to be an associative table.
     * @param classDefinition ClassDefinition to be transformed into an excel sheet (table)
     * @param workbook Representation of an Excel workbook (file)
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @return Representation of an Excel worksheet
     */
    public static HSSFSheet createTable(ClassDefinition classDefinition, HSSFWorkbook workbook) throws InstantiationException, IllegalAccessException {
        HSSFSheet workpage = workbook.createSheet(classDefinition.getTableName());
        HSSFRow excelRow = workpage.createRow(0);

        int columnNumber = 0;
        excelRow.createCell(columnNumber).setCellValue(IDCOLUMNNAME);
        columnNumber++;

        for (PropertyDefinition columnDefinition : classDefinition.getColumnDefinitions()) {
            if (!columnDefinition.isAssociativeTable()) {
                String columnName = columnDefinition.getColumnName();
                excelRow.createCell(columnNumber).setCellValue(columnName);
                columnNumber++;
            } else {
                createJoinTable(columnDefinition, workbook);
            }
        }
        return workpage;
    }

    /**
     * Creates a new excel sheet (table) for an associative columnDefinition and retrieves the joinColumnName and inverseJoinColumnName.
     * @param columnDefinition to be turned into an associative table.
     */
    protected static void createJoinTable(PropertyDefinition columnDefinition, HSSFWorkbook workbook) {
        if (columnDefinition instanceof JoinTable) {
            JoinTable joinTable = (JoinTable) columnDefinition;

            HSSFSheet workpage = workbook.createSheet(columnDefinition.getColumnName());
            HSSFRow excelRow = workpage.createRow(0);

            /* At this point a JoinTable can only have one joinColumnName and one inverseJoinColumnName. 
             * Later on, we're going to have to iterate over a set or list.
             */
            excelRow.createCell(0).setCellValue(joinTable.getJoinColumnName());
            excelRow.createCell(1).setCellValue(joinTable.getInverseJoinColumnName());
        } else {
            LOGGER.warn("Failed to cast [" + columnDefinition.getColumnName() + "] as JoinTable");
        }
    }

    /**
     * Writes the generated Excel file to the path specified in the fileOutputStream by the constructor.
     * @throws IOException Thrown when an I/O exception occurs
     */
    protected static void writeFile(HSSFWorkbook workbook, OutputStream os) throws IOException {
        try {
            workbook.write(os);
        } finally {
            os.close();
        }
    }

}
