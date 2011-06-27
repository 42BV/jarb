package org.jarb.populator.excel.mapping.exporter;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDatabaseType;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.PropertyPath;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.StringValue;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.utils.BeanPropertyUtils;

/**
 * Default implementation of {@link EntityExporter}.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class DefaultEntityExporter implements EntityExporter {
    /** Builds the excel template file. **/
    private ExcelTemplateBuilder excelTemplateBuilder = new ExcelTemplateBuilder();
    /** Resolves the row identifier of an entity. **/
    private EntityRowIdResolver entityRowIdResolver = new EntityRowIdResolver();
    /** Generates cell values for any type of property value. **/
    private CellValueGenerator cellValueGenerator;
    
    /**
     * Construct a new {@link DefaultEntityExporter}.
     * @param valueConversionService converts property values into any desired format
     */
    public DefaultEntityExporter(ValueConversionService valueConversionService) {
        cellValueGenerator = new CellValueGenerator(valueConversionService);
    }
    
    public void setExcelTemplateBuilder(ExcelTemplateBuilder excelTemplateBuilder) {
        this.excelTemplateBuilder = excelTemplateBuilder;
    }
    
    public void setEntityRowIdResolver(EntityRowIdResolver entityRowIdResolver) {
        this.entityRowIdResolver = entityRowIdResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Workbook export(EntityRegistry registry, MetaModel metamodel) {
        Workbook workbook = excelTemplateBuilder.createTemplate(metamodel);
        for (EntityDefinition<?> classDefinition : metamodel.entities()) {
            exportEntities(registry, classDefinition, workbook);
        }
        return workbook;
    }
    
    /**
     * Store the entities of a specific type in our sheet.
     * @param <T> type of entities being stored
     * @param registry registry containing the entities
     * @param classDefinition description of the entity class
     * @param workbook excel workbook that will contain our data
     */
    private <T> void exportEntities(EntityRegistry registry, EntityDefinition<T> classDefinition, Workbook workbook) {
        Sheet sheet = workbook.getSheet(classDefinition.getTableName());
        for(T entity : registry.forClass(classDefinition.getEntityClass())) {
            exportEntity(entity, classDefinition, sheet);
        }
    }
   
    /**
     * Store a specific entity in our sheet.
     * @param <T> type of entity being stored
     * @param entity the entity being stored
     * @param classDefinition description of the entity class
     * @param sheet the sheet in which we store the entity
     */
    private <T> void exportEntity(T entity, EntityDefinition<T> classDefinition, Sheet sheet) {
        Row row = sheet.createRow();
        // Handle each property definition
        for(PropertyDefinition propertyDefinition : classDefinition.properties()) {
            final PropertyDatabaseType type = propertyDefinition.getDatabaseType();
            if(type == PropertyDatabaseType.COLUMN) {
                // Retrieve the property value and store it as cell value
                Object propertyValue = getPropertyValue(entity, propertyDefinition);
                row.setCellValueAt(propertyDefinition.getColumnName(), cellValueGenerator.asCellValue(propertyValue));
            } else if(type == PropertyDatabaseType.JOIN_COLUMN) {
                // Retrieve the entity as property value and store its identifier
                Object referenceEntity = getPropertyValue(entity, propertyDefinition);
                if(referenceEntity != null) {
                    Object referenceIdentifier = entityRowIdResolver.resolveRowId(referenceEntity);
                    row.setCellValueAt(propertyDefinition.getColumnName(), cellValueGenerator.asCellValue(referenceIdentifier));
                }
            } else if(type == PropertyDatabaseType.JOIN_TABLE) {
                exportJoinTable(entity, propertyDefinition, sheet.getWorkbook());
            }
        }
        // Include the discriminator value, whenever relevant
        if(classDefinition.hasDiscriminatorColumn()) {
            String discriminatorValue = classDefinition.getDiscriminatorValue(entity.getClass());
            row.setCellValueAt(classDefinition.getDiscriminatorColumnName(), new StringValue(discriminatorValue));
        }
        // Define the row identifier of our entity, allowing us to reference it
        row.setCellValueAt(0, cellValueGenerator.asCellValue(entityRowIdResolver.resolveRowId(entity)));
    }
    
    /**
     * Store a collection of reference entities in a seperate "join" sheet.
     * This seperate sheet represents the join between two entities, as in
     * the relational database.
     * @param entity the entity containing our joins
     * @param propertyDefinition description of the join property
     * @param workbook the workbook that will contain our "join" sheet
     */
    private void exportJoinTable(Object entity, PropertyDefinition propertyDefinition, Workbook workbook) {
        Iterable<?> referenceEntities = (Iterable<?>) getPropertyValue(entity, propertyDefinition);
        if(referenceEntities != null) {
            Sheet joinSheet = workbook.getSheet(propertyDefinition.getJoinTableName());
            Object entityIdentifier = entityRowIdResolver.resolveRowId(entity);
            for(Object referenceEntity : referenceEntities) {
                Row joinRow = joinSheet.createRow();
                joinRow.setCellValueAt(0, cellValueGenerator.asCellValue(entityIdentifier));
                Object referenceIdentifier = entityRowIdResolver.resolveRowId(referenceEntity);
                joinRow.setCellValueAt(1, cellValueGenerator.asCellValue(referenceIdentifier));
            }
        }
    }
    
    /**
     * Retrieve the property value of an entity.
     * @param entity the entity that contains our value
     * @param propertyDefinition description of the property being retrieved
     * @return value of the property in our entity
     */
    private Object getPropertyValue(Object entity, PropertyDefinition propertyDefinition) {
        Object value = null;
        if(propertyDefinition.isEmbeddedAttribute()) {
            // Whenever our property is embedded, retrieve the embeddable that contains it
            final PropertyPath embeddablePath = propertyDefinition.getEmbeddablePath();
            if(BeanPropertyUtils.hasProperty(entity, embeddablePath.getStart().getName())) {
                Object leafEmbeddable = embeddablePath.traverse(entity);
                value = BeanPropertyUtils.getValue(leafEmbeddable, propertyDefinition.getName());
            }
        } else if(BeanPropertyUtils.hasProperty(entity, propertyDefinition.getName())) {
            value = BeanPropertyUtils.getValue(entity, propertyDefinition.getName());
        }
        return value;
    }

}
