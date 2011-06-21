//package org.jarb.populator.excel.workbook.generator;
//
//import java.io.IOException;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Set;
//
//import javax.persistence.PersistenceUnitUtil;
//import javax.persistence.metamodel.EntityType;
//import javax.persistence.metamodel.Metamodel;
//
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.jarb.populator.excel.DefaultExcelTestDataCase;
//import org.jarb.populator.excel.ExcelDataManager;
//import org.jarb.populator.excel.entity.query.DataReader;
//import org.jarb.populator.excel.metamodel.ClassDefinition;
//import org.jarb.populator.excel.metamodel.PropertyDefinition;
//import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
//import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
//import org.junit.Before;
//import org.junit.Test;
//
//public class AssociativeTableGeneratorTest extends DefaultExcelTestDataCase {
//    private ExcelDataManager excelTestData;
//
//    @Before
//    public void setupAssociativeTableGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
//            IllegalAccessException, InvocationTargetException {
//        excelTestData = getExcelDataManagerFactory().build();
//
//        //For code coverage purposes:
//        Constructor<AssociativeTableGenerator> constructor = AssociativeTableGenerator.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        constructor.newInstance();
//    }
//
//    @Test
//    public void testCreateAssociativeTables() throws InstantiationException, ClassNotFoundException, IllegalAccessException, InvalidFormatException,
//            IOException, NoSuchFieldException {
//        excelTestData.persistWorkbook("src/test/resources/Excel.xls");
//
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        PersistenceUnitUtil puUtil = getEntityManagerFactory().getPersistenceUnitUtil();
//        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
//
//        EntityType<?> entity = metamodel.entity(domain.entities.Employee.class);
//        ClassDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
//
//        BasicExcelFileGenerator.createJoinTable(classDefinition.getPropertyDefinition("projects"), workbook);
//
//        List<?> results = DataReader.getTableFromDatabase(getEntityManagerFactory(), entity);
//        int databaseRecordRow = 0;
//        Set<PropertyDefinition> associativeColumnDefinitions = ColumnDefinitionUtility.gatherAssociativeColumnDefinitions(classDefinition);
//        Object databaseRecord = results.get(databaseRecordRow);
//
//        AssociativeTableGenerator.createAssociativeTables(workbook, puUtil, associativeColumnDefinitions, databaseRecord);
//        workbook.getSheetAt(0).getRow(1);
//    }
//
//    @Test
//    public void testAssociativeColumnNameMistyped() throws InvalidFormatException, IOException, ClassNotFoundException, InstantiationException,
//            IllegalAccessException, NoSuchFieldException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
//        Class<?> persistentClass = domain.entities.Employee.class;
//        Field projectsField = persistentClass.getDeclaredField("projects");
//        PropertyDefinition joinTable = FieldAnalyzer.analyzeField(projectsField).build();
//
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet sheet = workbook.createSheet("Test");
//        HSSFRow row = sheet.createRow(0);
//        row.createCell(0).setCellValue("neithers_id");
//
//        Class<?>[] paramTypes = { PersistenceUnitUtil.class, Object.class, HSSFSheet.class, HSSFRow.class, PropertyDefinition.class, Object.class };
//        Object[] arguments = { null, null, sheet, null, joinTable, null };
//        Method setCellValueByProperType = AssociativeTableGenerator.class.getDeclaredMethod("createAssociativeCollectionRow", paramTypes);
//        setCellValueByProperType.setAccessible(true);
//        setCellValueByProperType.invoke(null, arguments);
//    }
//
//}
