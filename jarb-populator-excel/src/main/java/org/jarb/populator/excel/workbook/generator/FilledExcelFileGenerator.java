package org.jarb.populator.excel.workbook.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.hibernate.proxy.HibernateProxy;
import org.jarb.populator.excel.entity.query.DataReader;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.SubclassRetriever;
import org.jarb.populator.excel.util.ClassDefinitionNameComparator;
import org.jarb.populator.excel.workbook.validator.FieldValidator;
import org.jarb.utils.ReflectionUtils;

/**
 * This class reuses the Class- and ColumnDefinitions from the Metamodel to create a new Excel file filled with data from the database.
 * Also reuses the NewExcelFileGenerator to create the first row with columns.
 * @author Sander Benschop
 *
 */
public final class FilledExcelFileGenerator {

    /** Private constructor. */
    private FilledExcelFileGenerator() {
    }

    /**
     * Call this to initiate the Excelfile generation.
     * Creates a workpage for each classDefinition available. 
     * Associative tables are found as a @JoinColumn on the owning side but of course get their own workpage.
     * @param excelFileDestination Path of the Excel file being written to
     * @param entityManagerFactory EntityManagerFactory needed to read the data from the database
     * @throws ClassNotFoundException Thrown when the class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws IOException Thrown when an I/O error occurs
     * @throws NoSuchFieldException Thrown when a field cannot be found
     */
    public static void createFilledExcelFile(String excelFileDestination, EntityManagerFactory entityManagerFactory) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException, NoSuchFieldException {
        createFilledExcelFile(new FileOutputStream(excelFileDestination), entityManagerFactory);
    }

    /**
     * Call this to initiate the Excelfile generation.
     * Creates a workpage for each classDefinition available. 
     * Associative tables are found as a @JoinColumn on the owning side but of course get their own workpage.
     * @param outputStream Stream being written to
     * @param entityManagerFactory EntityManagerFactory needed to read the data from the database
     * @throws ClassNotFoundException Thrown when the class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws IOException Thrown when an I/O error occurs
     * @throws NoSuchFieldException Thrown when a field cannot be found
     */
    public static void createFilledExcelFile(OutputStream outputStream, EntityManagerFactory entityManagerFactory) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException, NoSuchFieldException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        List<ClassDefinition<?>> classDefinitions = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(entityManagerFactory);
        Collections.sort(classDefinitions, new ClassDefinitionNameComparator());

        for (ClassDefinition<?> classDefinition : classDefinitions) {
            createWorkpage(entityManagerFactory, classDefinition, workbook);
        }

        BasicExcelFileGenerator.writeFile(workbook, outputStream);
    }

    /**
     * CreateWorkpage is called for each ClassDefinition. It makes a sheet with all the ClassDefinition's ColumnDefinitions in it.
     * An exception are the associative ones: these are stored in their own Excel sheet.
     * @param entityManagerFactory EntityManagerFactory needed to read the data from the database
     * @param classDefinition Instance of ClassDefinition which is a representation of a database table
     * @param workbook HSSFWorkbook instance
     * @throws ClassNotFoundException Thrown when the class cannot be found
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field cannot be found
     */
    private static void createWorkpage(EntityManagerFactory entityManagerFactory, ClassDefinition<?> classDefinition, HSSFWorkbook workbook)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        PersistenceUnitUtil puUtil = entityManagerFactory.getPersistenceUnitUtil();
        EntityType<?> entity = metamodel.entity(classDefinition.getPersistentClass());
        BasicExcelFileGenerator.createTable(classDefinition, workbook);
        CellStyle dateFormatStyle = DateFormatStyle.getDateFormatStyle(workbook);
        Set<PropertyDefinition> associativeColumnDefinitions = ColumnDefinitionUtility.gatherAssociativeColumnDefinitions(classDefinition);

        List<?> results = DataReader.getTableFromDatabase(entityManagerFactory, entity);
        Integer numberofRows = results.size();
        HSSFRow row;
        int databaseRecordRow;
        for (int i = 1; i <= numberofRows; i++) {
            HSSFSheet sheet = workbook.getSheet(classDefinition.getTableName());
            row = sheet.createRow(i);
            databaseRecordRow = i - 1;

            Object databaseRecord = results.get(databaseRecordRow);

            createCellsForAllColumnsinSheet(classDefinition, puUtil, dateFormatStyle, row, sheet, databaseRecord);
            AssociativeTableGenerator.createAssociativeTables(workbook, puUtil, associativeColumnDefinitions, databaseRecord);
        }
    }

    /**
     * Creates Excel cells for all ColumnDefinitions from ClassDefinition that aren't from associative tables.
     * @param classDefinition Instance of ClassDefinition which is a representation of a database table
     * @param puUtil Utility to check an object's id
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param row The current Excel row
     * @param sheet The current Excel sheet
     * @param databaseRecord The current database record that is being read
     * @throws NoSuchFieldException Thrown when a field cannot be found
     */
    private static void createCellsForAllColumnsinSheet(ClassDefinition<?> classDefinition, PersistenceUnitUtil puUtil, CellStyle dateFormatStyle, HSSFRow row,
            HSSFSheet sheet, Object databaseRecord) throws NoSuchFieldException {
        String columnName;
        String fieldName;
        Class<?> persistentClass = databaseRecord.getClass();
        for (int columnNumber = 0; columnNumber < sheet.getRow(0).getPhysicalNumberOfCells(); columnNumber++) {
            //Get the column name
            columnName = sheet.getRow(0).getCell(columnNumber).getStringCellValue();
            fieldName = ColumnDefinitionUtility.getFieldName(classDefinition, columnName);
            if (FieldValidator.isExistingField(fieldName, persistentClass)) {
                createRegularFieldCell(puUtil, dateFormatStyle, row, fieldName, databaseRecord, columnNumber);
            } else if (classDefinition.getColumnDefinitionByFieldName(fieldName).isEmbeddedAttribute()) {
                createEmbeddedFieldCell(classDefinition, dateFormatStyle, row, columnName, fieldName, databaseRecord, columnNumber);
            } else if (fieldName.equals(classDefinition.getDiscriminatorColumnName())) {
                createDiscriminatorFieldCell(row, persistentClass, columnNumber);
            }
        }
    }

    /**
     * Creates an Excel cell for a regular field.
     * @param puUtil Utility to check an object's id
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param row The current Excel row
     * @param fieldName THe name of the field that is going to be saved
     * @param databaseRecord The current database record that is being read
     * @param columnNumber The column's horizontal position (0-based)
     */
    private static void createRegularFieldCell(PersistenceUnitUtil puUtil, CellStyle dateFormatStyle, HSSFRow row, String fieldName, Object databaseRecord,
            int columnNumber) {
        Object cellValue = ReflectionUtils.getFieldValue(databaseRecord, fieldName);

        if (cellValue instanceof HibernateProxy) {
            cellValue = ((HibernateProxy) cellValue).getHibernateLazyInitializer().getImplementation();
        }
        if (cellValue != null) {
            Class<?> cellValueType = cellValue.getClass();
            //If field is an @JoinColumn field:
            if (cellValueType.getAnnotation(javax.persistence.Entity.class) != null) {
                cellValue = puUtil.getIdentifier(cellValue);
            }

            //If field is an Enumeration the String value must be retrieven from the cellValue.
            if (cellValue instanceof Enum<?>) {
                cellValue = cellValue.toString();
            }

            CellValueSetter.setCellValueByProperType(row, columnNumber, cellValue, dateFormatStyle);
        }
    }

    /**
     * Creates an Excel cell for an embedded field.
     * @param classDefinition Instance of ClassDefinition which is a representation of a database table
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param row The current Excel row
     * @param fieldName THe name of the field that is going to be saved
     * @param databaseRecord The current database record that is being read
     * @param columnNumber The column's horizontal position (0-based)
     * @param columnName The name of the column
     */
    private static void createEmbeddedFieldCell(ClassDefinition<?> classDefinition, CellStyle dateFormatStyle, HSSFRow row, String columnName, String fieldName,
            Object databaseRecord, int columnNumber) {
        String embeddedObjectName = classDefinition.getColumnDefinitionByColumnName(columnName).getEmbeddedObjectName();
        Object embeddedObject = ReflectionUtils.getFieldValue(databaseRecord, embeddedObjectName);
        if (embeddedObject != null) {
            Object cellValue = ReflectionUtils.getFieldValue(embeddedObject, fieldName);
            if (cellValue != null) {
                CellValueSetter.setCellValueByProperType(row, columnNumber, cellValue, dateFormatStyle);
            }
        }
    }

    /**
     * Creates an Excel cell for a discriminator field.
     * @param row The current Excel row
     * @param persistentClass The persistent class to get the discriminator value from
     * @param columnNumber The column's horizontal position (0-based)
     */
    private static void createDiscriminatorFieldCell(HSSFRow row, Class<?> persistentClass, int columnNumber) {
        //The discriminator value is not available in the databaseRecord. Fortunately, we have the persistent class to get it from.
        String discriminatorValue = SubclassRetriever.getDiscriminatorValue(persistentClass);
        row.createCell(columnNumber).setCellValue(discriminatorValue);
    }
}
