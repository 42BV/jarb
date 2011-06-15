package org.jarb.populator.excel.workbook.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ClassDefinitionNameComparator;
import org.jarb.populator.excel.metamodel.MetaModel;

/**
 * Generates a new Excel file with only tablenames and columnnames in it based on the mapping in the domain package.
 * @author Sander Benschop
 *
 */
public final class NewExcelFileGenerator {

    /** Private constructor. */
    private NewExcelFileGenerator() {
    }

    /**
     * Calls the create table for each classDefinition and calls the write file function.
     * @param excelFileDestination Path to the Excel file being written to
     * @param entityManagerFactory EntityManagerFactory from ApplicationContext file
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws IOException Thrown when an I/O exception occurs.
     * @throws ClassNotFoundException Throws if a class cannot be found or if domain name is incorrect
     * @throws NoSuchFieldException Thrown if a field cannot be found
     */
    public static void createEmptyXLS(String excelFileDestination, MetaModel metamodel) throws InstantiationException, IllegalAccessException, IOException,
            ClassNotFoundException, NoSuchFieldException {
        createEmptyXLS(new FileOutputStream(excelFileDestination), metamodel);
    }

    /**
     * Calls the create table for each classDefinition and calls the write file function.
     * @param outputStream Stream being written to
     * @param entityManagerFactory EntityManagerFactory from ApplicationContext file
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws IOException Thrown when an I/O exception occurs.
     * @throws ClassNotFoundException Throws if a class cannot be found or if domain name is incorrect
     * @throws NoSuchFieldException Thrown if a field cannot be found
     */
    public static void createEmptyXLS(OutputStream outputStream, MetaModel metamodel) throws InstantiationException, IllegalAccessException, IOException,
            ClassNotFoundException, NoSuchFieldException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        List<ClassDefinition<?>> classDefinitions = new ArrayList<ClassDefinition<?>>(metamodel.getClassDefinitions());
        Collections.sort(classDefinitions, new ClassDefinitionNameComparator());

        for (ClassDefinition<?> classDefinition : classDefinitions) {
            BasicExcelFileGenerator.createTable(classDefinition, workbook);
        }
        BasicExcelFileGenerator.writeFile(workbook, outputStream);
    }

}
