package org.jarbframework.populator.excel.mapping.importer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.entity.EntityTable;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;
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
    private Map<Definition, Map<Object, ExcelRow>> excelRowMap;
    private Map<ElementCollectionDefinition<?>, Map<Object, List<Object>>> elementCollectionRowMap;

    public ExcelImporter(ValueConversionService conversionService, EntityManagerFactory entityManagerFactory) {
        valueStorer = new StoreExcelRecordValue(conversionService);
        this.entityManagerFactory = entityManagerFactory;
        excelRowMap = new HashMap<Definition, Map<Object, ExcelRow>>();
        elementCollectionRowMap = new HashMap<ElementCollectionDefinition<?>, Map<Object, List<Object>>>();
    }

    /**
     * Returns an EntityRegistry containing EntityTables which in their turn contain ExcelRows.
     * @param excel Excel file in use to be stored in the objectModel
     * @param entityDefinitions Used to create EntityTables
     * @return EntityRegistry containing EntityTables and parsed excel records.
     * @throws NoSuchFieldException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EntityRegistry parseExcelToRegistry(Workbook excel, Collection<Definition> entityDefinitions) throws NoSuchFieldException {
        //First we need to create all ExcelRows, including those of the subtype 'ElementCollectionDefinitions'
        createExcelRows(excel, entityDefinitions);

        EntityRegistry entityRegistry = new EntityRegistry();
        //First loop over the entities
        for (Definition entityDefinition : entityDefinitions) {
            if (entityDefinition instanceof EntityDefinition<?>) {
                final Class entityClass = JpaUtils.getDefinedClassOfDefinition(entityDefinition);
                EntityTable<Object> entities = new EntityTable<Object>(entityClass);
                for (Entry<Object, ExcelRow> entry : excelRowMap.get(entityDefinition).entrySet()) {
                    ExcelRow excelRow = entry.getValue();
                    //First map the foreign relations
                    ForeignRelationsMapper.makeForeignRelations(excelRow, excelRowMap);
                    ForeignRelationsMapper.addElementCollectionRows((EntityDefinition<?>) entityDefinition, excelRow, entry.getKey(), elementCollectionRowMap);

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
        }
        return entityRegistry;
    }

    /**
     * Creates the ExcelRows with data from the Excel workbook.
     * @param excel ExcelWorkbook to load data from
     * @param entityDefinitions Definitions of tables data will be stored in
     * @return
     */
    private void createExcelRows(final Workbook excel, final Collection<Definition> entityDefinitions) {
        for (Definition entityDefinition : entityDefinitions) {
            excelRowMap.put(entityDefinition, parseWorksheet(excel, entityDefinition));
        }
    }

    /**
     * Returns a hashmap containing Excel records and their cell values.
     * @param excel Excel file in use to be stored in the objectModel
     * @param definition Definition containing the columnDefinitions the values will be stored in
     * @return Hashmap containing Excel records and their cell values
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public Map<Object, ExcelRow> parseWorksheet(final Workbook excel, final Definition definition) {
        Map<Object, ExcelRow> createdInstances = new HashMap<Object, ExcelRow>();
        Map<Object, List<Object>> createdElementCollectionInstances = new HashMap<Object, List<Object>>();

        Sheet sheet = excel.getSheet(definition.getTableName());
        String discriminatorColumnName = getDiscriminatorColumnFromDefinition(definition);

        if (sheet != null) {
            for (Integer rowPosition = 1; rowPosition <= sheet.getLastRowNumber(); rowPosition++) {
                logger.debug("Importing row {}", rowPosition);
                ExcelRow excelRow = new ExcelRow(determineEntityClass(sheet, definition, discriminatorColumnName, rowPosition));
                storeExcelRecordByColumnDefinitions(excel, definition, rowPosition, excelRow);
                putCreatedInstance(sheet, definition, createdInstances, createdElementCollectionInstances, rowPosition, excelRow);
            }
        }

        if ((definition instanceof ElementCollectionDefinition<?>) && (!createdElementCollectionInstances.isEmpty())) {
            elementCollectionRowMap.put((ElementCollectionDefinition<?>) definition, createdElementCollectionInstances);
        }
        return createdInstances;
    }

    /**
     * Returns the discriminator column name from Definition if it's an EntityDefinition.
     * @param definition Definition of either an Entity or ElementCollection embeddable
     * @return Discriminator name
     */
    private String getDiscriminatorColumnFromDefinition(Definition definition) {
        String discriminatorColumnName = null;
        if (definition instanceof EntityDefinition<?>) {
            EntityDefinition<?> entityDefinition = (EntityDefinition<?>) definition;
            discriminatorColumnName = entityDefinition.getDiscriminatorColumnName();
        }
        return discriminatorColumnName;
    }

    /**
     * Determines the entity class of the row being persisted. Could be the Definition's defined class or a subclass.
     * @param sheet Sheet to get WorkbookDefinitions from
     * @param definition definition used to get the defined class from.
     * @param discriminatorColumnName DiscriminatorColumnName is used to determine the subtype.
     * @param rowPosition The row number which is currently being processed.
     * @return Entity class
     */
    private Class<?> determineEntityClass(final Sheet sheet, final Definition definition, String discriminatorColumnName, Integer rowPosition) {
        Class<?> entityClass = JpaUtils.getDefinedClassOfDefinition(definition);
        if (discriminatorColumnName != null) {
            WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(definition, sheet.getWorkbook());
            Integer discriminatorPosition = worksheetDefinition.getColumnPosition(discriminatorColumnName);
            String discriminatorValue = getDiscriminatorValueFromExcelFile(sheet, rowPosition, discriminatorPosition);
            if ((discriminatorValue != null) && (definition instanceof EntityDefinition<?>)) {
                EntityDefinition<?> entityDefinition = (EntityDefinition<?>) definition;
                Class<?> subClass = entityDefinition.getEntitySubClass(discriminatorValue);
                if (subClass != null) {
                    entityClass = subClass;
                }
            }

        }
        return entityClass;
    }

    /**
     * Returns the discriminatorValue from the Excelsheet.
     * @param sheet Sheet to get the values from
     * @param rowPosition Row number
     * @param discriminatorPosition Column number
     * @return Discriminator value
     */
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
    private void putCreatedInstance(final Sheet sheet, final Definition classDefinition, Map<Object, ExcelRow> createdInstances,
            Map<Object, List<Object>> createdElementCollectionInstances, Integer rowPosition,
            ExcelRow excelRow) {
        if (classDefinition instanceof EntityDefinition<?>) {
            Object identifier = getIdentifierValue(rowPosition, sheet, sheet.getColumnNameAt(0));
            addRecordIfIdentifierIsUnique(sheet, classDefinition, createdInstances, createdElementCollectionInstances, rowPosition, excelRow, identifier);
        } else if (classDefinition instanceof ElementCollectionDefinition<?>) {
            ElementCollectionDefinition<?> elementCollection = (ElementCollectionDefinition<?>) classDefinition;
            Class<?> enclosingClass = elementCollection.getEnclosingClass();
            Class<?> beanClass = elementCollection.getDefinedClass();
            Map<String, Object> identifiers = getIdentifiersFromElementCollectionField(sheet, rowPosition, enclosingClass, beanClass);
            addRecordIfIdentifierIsUnique(sheet, classDefinition, createdInstances, createdElementCollectionInstances, rowPosition, excelRow, identifiers);
        } else {
            logger.error("Could not store row #{} of {}, because the Definition is of an improper type.", new Object[] { rowPosition, sheet.getName() });
        }
    }

    /**
     * Returns the temporary identifier value of the row. 
     * If it's a number it will be converted to an integer so the difference in 
     * formatting throughout multiple Excel files won't cause foreign key couplings to fail.
     * @param rowPosition The current row position
     * @param sheet Excel worksheet to gather data from
     * @param columnName The column name to get the value from
     * @return Identifier value
     */
    private Object getIdentifierValue(int rowPosition, final Sheet sheet, String columnName) {
        Object value = sheet.getCellAt(rowPosition, columnName).getValue();
        if (value instanceof Number) {
            value = ((Number) value).intValue();
        }
        return value;
    }

    /**
     * Returns the identifiers from an ElementCollectionField by using the @JoinColumn annotations. 
     * If no annotations are present, the default JPA column names will be used to find the data.
     * @param sheet ExcelSheet to get the data from
     * @param rowPosition The current row position
     * @param enclosingClass The class the ElementCollection is enclosed in
     * @param beanClass The ElementCollection's class
     * @return Map of columnnames and identifiers
     */
    private Map<String, Object> getIdentifiersFromElementCollectionField(final Sheet sheet,
            Integer rowPosition, Class<?> enclosingClass, Class<?> beanClass) {
        Map<String, Object> identifiers = new HashMap<String, Object>();
        Field elementCollectionField = getElementCollectionField(beanClass, enclosingClass);
        if (elementCollectionField.getAnnotation(CollectionTable.class) != null) {
            CollectionTable collectionTable = (CollectionTable) elementCollectionField.getAnnotation(CollectionTable.class);
            for (JoinColumn joinColumn : collectionTable.joinColumns()) {
                Object identifierValue = getIdentifierValue(rowPosition, sheet, joinColumn.name());
                identifiers.put(joinColumn.name(), identifierValue);
            }
        }

        if (identifiers.isEmpty()) {
            /*If no @JoinColumns are present in the CollectionTable or the CollectionTable isn't present at all, 
            the identifier will be assumed to be the name of the enclosing entity and the
            primary key columns separated by an underscore as documented in the JPA spec. */
            String identifierColumn = JpaMetaModelUtils.deduceIdentifierColumnName(enclosingClass, entityManagerFactory);
            identifiers.put(identifierColumn, getIdentifierValue(rowPosition, sheet, identifierColumn));
        }
        return identifiers;
    }
    
    /**
     * Adds the record to the proper map if its identifiers are unique. In case of an ElementCollectionDefinition row it will always be added.
     * @param sheet Excelsheet to get the sheetname from
     * @param classDefinition Definition which belongs to the ExcelRow
     * @param createdInstances Map of created ExcelRow instances
     * @param createdElementCollectionInstances Map of created ExcelRow instances of ElementCollection type
     * @param rowPosition Current row position
     * @param excelRow Excelrow which is being processed
     * @param identifier Identifier of ExcelRow
     */
    private void addRecordIfIdentifierIsUnique(final Sheet sheet, final Definition classDefinition, Map<Object, ExcelRow> createdInstances,
            Map<Object, List<Object>> createdElementCollectionInstances,
            Integer rowPosition, ExcelRow excelRow, Object identifier) {
        if (!isInvalidIdentifier(identifier)) {
            logger.error("Could not store row #{} of {}, because the identifier is empty.", new Object[] { rowPosition, sheet.getName() });
        } else if (classDefinition instanceof EntityDefinition<?>) {
            if (!createdInstances.containsKey(identifier)) {
                createdInstances.put(identifier, excelRow);
            } else {
                logger.error("IDCOLUMNNAME value '" + identifier + "' in table " + classDefinition.getTableName() + " is not unique.");
            }
        } else if (classDefinition instanceof ElementCollectionDefinition<?>) {
            if (createdElementCollectionInstances.containsKey(identifier)) {
                createdElementCollectionInstances.get(identifier).add(excelRow.getCreatedInstance());
            } else {
                List<Object> excelRowList = new ArrayList<Object>();
                excelRowList.add(excelRow.getCreatedInstance());
                createdElementCollectionInstances.put(identifier, excelRowList);
            }
        }
    }

    /**
     * Checks if the passed identifier is valid by checking if it's null or empty.
     * @param identifier Identifier to check validity.
     * @return True if identifier is not null or empty.
     */
    private boolean isInvalidIdentifier(Object identifier) {
        boolean isValid = false;
        if (identifier instanceof Collection) {
            isValid = (!((Collection<?>) identifier).isEmpty());
        } else {
            isValid = identifier != null;
        }
        return isValid;
    }

    /**
     * Returns the ElementCollectionField from the enclosingClass.
     * @param beanClass The type class the ElementCollection is of.
     * @param enclosingClass The class to search through.
     * @return ElementCollection field.
     */
    private Field getElementCollectionField(Class<?> beanClass,
            Class<?> enclosingClass) {
        try {
            return enclosingClass.getDeclaredField(JpaMetaModelUtils.getFieldNameForElementCollectionClass(beanClass, enclosingClass, entityManagerFactory));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stores an Excelrecord by ColumnDefinition.
     * @param sheet Excel file
     * @param definition ClassDefintiion
     * @param rowPosition The number that represents the current row (0 based!)
     * @param excelRow The excelRecord to save the data to
     * @throws NoSuchFieldException Thrown if field cannot be found
     */
    private void storeExcelRecordByColumnDefinitions(final Workbook excel, final Definition definition, Integer rowPosition, ExcelRow excelRow) {
        for (PropertyDefinition columnDefinition : definition.properties()) {
            valueStorer.storeValue(excel, definition, columnDefinition, rowPosition, excelRow);
        }
    }
}
