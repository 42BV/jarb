package org.jarbframework.populator.excel.mapping.exporter;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyPath;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.StringValue;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.utils.bean.ModifiableBean;

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
        for (Definition entityDefinition : metamodel.entities()) {
            exportEntities(registry, entityDefinition, workbook);
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
    private <T> void exportEntities(EntityRegistry registry, Definition classDefinition, Workbook workbook) {
        Sheet sheet = workbook.getSheet(classDefinition.getTableName());

        for (Object entity : registry.withClass(JpaUtils.getDefinedClassOfDefinition(classDefinition))) {
            exportEntity(entity, classDefinition, sheet);
        }
        
    }

    /**
     * Store a specific entity in our sheet.
     * @param <T> type of entity being stored
     * @param entity the entity being stored
     * @param definition description of the entity class
     * @param sheet the sheet in which we store the entity
     */
    private <T> void exportEntity(T entity, Definition definition, Sheet sheet) {
        Row row = sheet.createRow();
        // Handle each property definition
        for (PropertyDefinition propertyDefinition : definition.properties()) {
            final PropertyDatabaseType type = propertyDefinition.getDatabaseType();
            if (type == PropertyDatabaseType.COLUMN) {
                // Retrieve the property value and store it as cell value
                Object propertyValue = getPropertyValue(entity, propertyDefinition);
                row.setCellValueAt(propertyDefinition.getColumnName(), cellValueGenerator.asCellValue(propertyValue));
            } else if (type == PropertyDatabaseType.REFERENCE) {
                // Retrieve the entity as property value and store its identifier
                Object referenceEntity = getPropertyValue(entity, propertyDefinition);
                if (referenceEntity != null) {
                    Object referenceIdentifier = entityRowIdResolver.resolveRowId(referenceEntity);
                    row.setCellValueAt(propertyDefinition.getColumnName(), cellValueGenerator.asCellValue(referenceIdentifier));
                }
            } else if (type == PropertyDatabaseType.COLLECTION_REFERENCE) {
                exportJoinTable(entity, propertyDefinition, sheet.getWorkbook());
            }
        }

        if (definition instanceof EntityDefinition<?>) {
            EntityDefinition<?> entityDefinition = (EntityDefinition<?>) definition;
            // Include the discriminator value, whenever relevant
            if (entityDefinition.hasDiscriminatorColumn()) {
                String discriminatorValue = entityDefinition.getDiscriminatorValue(entity.getClass());
                row.setCellValueAt(entityDefinition.getDiscriminatorColumnName(), new StringValue(discriminatorValue));
            }
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
        if (referenceEntities != null) {
            Sheet joinSheet = workbook.getSheet(propertyDefinition.getJoinTableName());
            Object entityIdentifier = entityRowIdResolver.resolveRowId(entity);
            for (Object referenceEntity : referenceEntities) {
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
        ModifiableBean<Object> propertyAccessor = ModifiableBean.wrap(entity);
        if (propertyDefinition.isEmbeddedAttribute()) {
            // Whenever our property is embedded, retrieve the container
            final PropertyPath embeddablePath = propertyDefinition.getEmbeddablePath();
            if (propertyAccessor.isReadableProperty(embeddablePath.getStart().getName())) {
                Object leafEmbeddable = embeddablePath.traverse(entity);
                ModifiableBean<Object> leafAccessor = ModifiableBean.wrap(leafEmbeddable);
                value = leafAccessor.getPropertyValue(propertyDefinition.getName());
            }
        } else if (propertyAccessor.isReadableProperty(propertyDefinition.getName())) {
            value = propertyAccessor.getPropertyValue(propertyDefinition.getName());
        }
        return value;
    }

}
