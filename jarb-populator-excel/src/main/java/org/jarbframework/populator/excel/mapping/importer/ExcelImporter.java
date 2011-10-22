package org.jarbframework.populator.excel.mapping.importer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.entity.EntityTable;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses through Excel data. Can create a set of columnDefinitions and add to a columnDefinition.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class ExcelImporter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImporter.class);
    private StoreExcelRecordValue valueStorer;
    private EntityManagerFactory entityManagerFactory;
    private Map<EntityDefinition<?>, Map<Object, ExcelRow>> excelRowMap;

    public ExcelImporter(ValueConversionService conversionService, EntityManagerFactory entityManagerFactory) {
        valueStorer = new StoreExcelRecordValue(conversionService);
        this.entityManagerFactory = entityManagerFactory;
        excelRowMap = new HashMap<EntityDefinition<?>, Map<Object, ExcelRow>>();
    }

    /**
     * Returns an EntityRegistry containing EntityTables which in their turn contain ExcelRows
     * @param excel Excel file in use to be stored in the objectModel
     * @param entityDefinitions Used to creaet EntityTables
     * @return EntityRegistry containing EntityTables and parsed excel records.
     * @throws NoSuchFieldException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EntityRegistry parseExcelToRegistry(Workbook excel, Collection<EntityDefinition<?>> entityDefinitions) throws NoSuchFieldException {
    	//First we need to create all ExcelRows..
    	createExcelRows(excel, entityDefinitions);
    	
    	EntityRegistry entityRegistry = new EntityRegistry();
        //First loop over the entities formerly known as classDefinitions
        for (EntityDefinition<?> entityDefinition : entityDefinitions) {
            final Class entityClass = entityDefinition.getEntityClass();
            EntityTable<Object> entities = new EntityTable<Object>(entityClass);
            for (ExcelRow excelRow : excelRowMap.get(entityDefinition).values()) {
            	//First map the foreign relations
            	ForeignRelationsMapper.makeForeignRelations(excelRow, excelRowMap);

                // Entity registry uses the entities persistent identifier, rather than its row identifier
                // Row identifier is only used in excel, entity identifier is used in registry and database
                Object entity = excelRow.getCreatedInstance();
                Object identifier = JpaUtils.getIdentifier(entity, entityManagerFactory);
                if (identifier == null) {
                    // Whenever the identifier is null, because it has not yet been defined (generated value)
                    // use a random placeholder identifier. This identifier is only used to access the entity
                    // inside the generated entity registry. After persisting the registry, our entity identifier
                    // will be replaced with the actual database identifier.
                    identifier = UUID.randomUUID().toString();
                }
                entities.add(identifier, entity);
            }
            entityRegistry.addAll(entities);
        }
        return entityRegistry;
    }

    private Map<EntityDefinition<?>, Map<Object, ExcelRow>> createExcelRows (final Workbook excel, final Collection<EntityDefinition<?>> entityDefinitions){
    	for (EntityDefinition<?> entityDefinition : entityDefinitions){
    		excelRowMap.put(entityDefinition, parseWorksheet(excel, entityDefinition));
    	}
    	return excelRowMap;
    }

    /**
     * Returns a hashmap containing Excel records and their cell values.
     * @param excel Excel file in use to be stored in the objectModel
     * @param classDefinition ClassDefinition containing the columnDefinitions the values will be stored in
     * @return Hashmap containing Excel records and their cell values
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public Map<Object, ExcelRow> parseWorksheet(final Workbook excel, final EntityDefinition<?> classDefinition) {
        Map<Object, ExcelRow> createdInstances = new HashMap<Object, ExcelRow>();

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        if (sheet != null) {
            String discriminatorColumnName = classDefinition.getDiscriminatorColumnName();
            for (Integer rowPosition = 1; rowPosition <= sheet.getLastRowNumber(); rowPosition++) {
                logger.debug("Importing row {}", rowPosition);
                ExcelRow excelRow = new ExcelRow(determineEntityClass(sheet, classDefinition, discriminatorColumnName, rowPosition));
                storeExcelRecordByColumnDefinitions(excel, classDefinition, rowPosition, excelRow);
                putCreatedInstance(sheet, classDefinition, createdInstances, rowPosition, excelRow);
            }
        }

        return createdInstances;
    }

    private Class<?> determineEntityClass(final Sheet sheet, final EntityDefinition<?> classDefinition, String discriminatorColumnName, Integer rowPosition) {
        Class<?> entityClass = classDefinition.getEntityClass();
        if (discriminatorColumnName != null) {
            WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, sheet.getWorkbook());
            Integer discriminatorPosition = worksheetDefinition.getColumnPosition(discriminatorColumnName);
            String discriminatorValue = getDiscriminatorValueFromExcelFile(sheet, rowPosition, discriminatorPosition);
            if (discriminatorValue != null) {
                Class<?> subClass = classDefinition.getEntitySubClass(discriminatorValue);
                if (subClass != null) {
                    entityClass = subClass;
                }
            }
        }
        return entityClass;
    }

    private String getDiscriminatorValueFromExcelFile(final Sheet sheet, Integer rowPosition, Integer discriminatorPosition) {
        String discriminatorValue = null;
        if (discriminatorPosition != null) {
            discriminatorValue = (String) sheet.getValueAt(rowPosition, discriminatorPosition);
        }
        return discriminatorValue;
    }

    /**
     * Puts a new instance in the value map.
     * @param sheet Excel file the data is gathered from
     * @param classDefinition ClassDefinition representing a database table
     * @param createdInstances A map of created instances
     * @param rowPosition The rowPosition in the Excel file (0 based) 
     * @param excelRow An excelRow to store in the value map
     */
    private void putCreatedInstance(final Sheet sheet, final EntityDefinition<?> classDefinition, Map<Object, ExcelRow> createdInstances, Integer rowPosition,
            ExcelRow excelRow) {
        Object identifier = sheet.getValueAt(rowPosition, 0);
        if (identifier == null) {
            logger.error("Could not store row #{} of {}, because the identifier is empty.", new Object[] { rowPosition, sheet.getName() });
        } else {
            if (!createdInstances.containsKey(identifier)) {
                createdInstances.put(identifier, excelRow);
            } else {
                logger.error("IDCOLUMNNAME value '" + identifier + "' in table " + classDefinition.getTableName() + " is not unique.");
            }
        }
    }

    /**
     * Stores an Excelrecord by ColumnDefinition.
     * @param sheet Excel file
     * @param classDefinition ClassDefintiion
     * @param rowPosition The number that represents the current row (0 based!)
     * @param excelRow The excelRecord to save the data to
     * @throws NoSuchFieldException Thrown if field cannot be found
     */
    private void storeExcelRecordByColumnDefinitions(final Workbook excel, final EntityDefinition<?> classDefinition, Integer rowPosition, ExcelRow excelRow) {
        for (PropertyDefinition columnDefinition : classDefinition.properties()) {
            valueStorer.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        }
    }
}