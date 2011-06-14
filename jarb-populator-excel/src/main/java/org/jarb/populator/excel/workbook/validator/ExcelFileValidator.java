package org.jarb.populator.excel.workbook.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.JoinTable;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.util.ClassDefinitionNameComparator;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Class that can verify an Excel file. Checks Excel files by calling functions from ColumnValidator.java and SheetValidator.java
 * @author Sander Benschop
 *
 */
public final class ExcelFileValidator {

    private ExcelFileValidator() {
    }

    /** Identification number of row in Excel file. */
    private static final String IDCOLUMNNAME = "id";

    /**
     * Starts the verification process by opening the specified Excel file and checking if it's columns and sheets match the mapping's.
     * @param workbook excel workbook
     * @param metamodel entity context meta model
     * @return List of verification outcomes, these can either be error messages or messages that everything is fine
     * @throws InvalidFormatException Thrown when the file has the wrong file format (Microsoft Excel 97-2003 format is supported)
     * @throws IOException Thrown when an I/O exception occurs
     * @throws ClassNotFoundException Throws if a class cannot be found
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws InstantiationException Thrown if used on a class that cannot be instantiated (abstract or interface)
     * @throws NoSuchFieldException Thrown if a field cannot be found
     */
    public static List<String> verify(Workbook workbook, MetaModel metamodel) throws InvalidFormatException, IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        List<ClassDefinition<?>> classDefinitions = new ArrayList<ClassDefinition<?>>(metamodel.getClassDefinitions());
        Collections.sort(classDefinitions, new ClassDefinitionNameComparator());

        List<String> verificationOutcomes = new ArrayList<String>();
        Set<String> excelSheets = new HashSet<String>();

        for (ClassDefinition<?> classDefinition : classDefinitions) {
            Set<String> columnNames = new HashSet<String>();
            columnNames.addAll(determineColumnAndSheetsNames(workbook, verificationOutcomes, excelSheets, classDefinition));
            verificationOutcomes.addAll(ColumnValidator.validateColumnsInSheet(workbook, columnNames, classDefinition.getTableName()));
        }

        verificationOutcomes.addAll(SheetValidator.checkSheetAvailability(workbook, excelSheets));
        return verificationOutcomes;
    }

    /**
     * Determines the column- and sheetnames for an Excel file.
     * Combined class because otherwise the iteration would have to be done twice
     * @param excel Excelfile to be analyzed
     * @param verificationOutcomes String list of verification outcomes from verify function to be further updated
     * @param excelSheets List of sheet names to be further updated
     * @param classDefinition Classdefinition to get columnDefinitions and tableNames from
     * @return Set of columnNames
     */
    private static Set<String> determineColumnAndSheetsNames(Workbook excel, List<String> verificationOutcomes, Set<String> excelSheets,
            ClassDefinition<?> classDefinition) {
        Set<String> columnNames = new HashSet<String>();
        columnNames.add(IDCOLUMNNAME);
        for (PropertyDefinition columnDefinition : classDefinition.getColumnDefinitions()) {
            if (columnDefinition.isAssociativeTable()) {
                Set<String> associativeColumnNames = new HashSet<String>();
                // Check associative table
                JoinTable joinTable = (JoinTable) columnDefinition;
                associativeColumnNames.add(joinTable.getJoinColumnName());
                associativeColumnNames.add(joinTable.getInverseJoinColumnName());
                verificationOutcomes.addAll(ColumnValidator.validateColumnsInSheet(excel, associativeColumnNames, columnDefinition.getColumnName()));
                excelSheets.add(columnDefinition.getColumnName());
            } else {
                columnNames.add(columnDefinition.getColumnName());
                excelSheets.add(classDefinition.getTableName());
            }
        }
        return columnNames;
    }
}
