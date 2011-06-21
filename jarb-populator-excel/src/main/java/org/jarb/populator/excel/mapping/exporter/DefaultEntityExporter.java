package org.jarb.populator.excel.mapping.exporter;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnType;
import org.jarb.populator.excel.metamodel.FieldPath;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.util.JpaUtils;
import org.jarb.populator.excel.workbook.BooleanValue;
import org.jarb.populator.excel.workbook.CellValue;
import org.jarb.populator.excel.workbook.DateValue;
import org.jarb.populator.excel.workbook.EmptyValue;
import org.jarb.populator.excel.workbook.NumericValue;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.StringValue;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.utils.BeanPropertyHandler;
import org.jarb.utils.ReflectionUtils;

/**
 * Default implementation of {@link EntityExporter}.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class DefaultEntityExporter implements EntityExporter {
    private EntityManagerFactory entityManagerFactory;
    private ValueConversionService valueConversionService;
    
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    public void setValueConversionService(ValueConversionService valueConversionService) {
        this.valueConversionService = valueConversionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workbook export(EntityRegistry registry, MetaModel metamodel) {
        Workbook workbook = new Workbook();
        for (ClassDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
            createEntitySheet(workbook, registry, classDefinition);
        }
        return workbook;
    }
    
    private <T> void createEntitySheet(Workbook workbook, EntityRegistry registry, ClassDefinition<T> classDefinition) {
        Sheet sheet = workbook.createSheet(classDefinition.getTableName());
        storeColumnNames(sheet, classDefinition);
        for(T entity : registry.getAll(classDefinition.getPersistentClass())) {
            storeEntity(entity, sheet, classDefinition, registry);
        }
    }
    
    private void storeColumnNames(Sheet sheet, ClassDefinition<?> classDefinition) {
        int columnNumber = 0;
        for(String columnName : classDefinition.getColumnNames()) {
            sheet.setColumnNameAt(columnNumber++, columnName);
        }
    }
    
    private <T> void storeEntity(T entity, Sheet sheet, ClassDefinition<T> classDefinition, EntityRegistry registry) {
        Row row = sheet.createRow();
        for(PropertyDefinition propertyDefinition : classDefinition.getPropertyDefinitions()) {
            final ColumnType columnType = propertyDefinition.getColumnType();
            if(columnType == ColumnType.BASIC) {
                // Retrieve the property value and store it as cell value
                Object propertyValue = getPropertyValue(entity, propertyDefinition);
                row.setCellValueAt(propertyDefinition.getColumnName(), createCellValue(propertyValue));
            } else if(columnType == ColumnType.JOIN_COLUMN) {
                // Retrieve the entity as property value and store its identifier
                Object referenceEntity = getPropertyValue(entity, propertyDefinition);
                Object referenceIdentifier = JpaUtils.getIdentifier(referenceEntity, entityManagerFactory);
                row.setCellValueAt(propertyDefinition.getColumnName(), createCellValue(referenceIdentifier));
            } else if(columnType == ColumnType.JOIN_TABLE) {
                // Store all reference entities in a seperate join sheet
                Sheet joinSheet = sheet.getWorkbook().createSheet(propertyDefinition.getJoinTableName());
                joinSheet.setColumnNameAt(0, propertyDefinition.getJoinColumnName());
                joinSheet.setColumnNameAt(1, propertyDefinition.getInverseJoinColumnName());
                final Object entityIdentifier = JpaUtils.getIdentifier(entity, entityManagerFactory);
                Iterable<?> referenceEntities = (Iterable<?>) getPropertyValue(entity, propertyDefinition);
                for(Object referenceEntity : referenceEntities) {
                    Row joinRow = joinSheet.createRow();
                    joinRow.setCellValueAt(0, createCellValue(entityIdentifier));
                    Object referenceIdentifier = JpaUtils.getIdentifier(referenceEntity, entityManagerFactory);
                    joinRow.setCellValueAt(1, createCellValue(referenceIdentifier));
                }
            }
        }
        if(classDefinition.hasDiscriminatorColumn()) {
            final String discriminatorValue = classDefinition.getDiscriminatorValue(entity.getClass());
            row.setCellValueAt(classDefinition.getDiscriminatorColumnName(), new StringValue(discriminatorValue));
        }
    }
    
    private Object getPropertyValue(Object entity, PropertyDefinition propertyDefinition) {
        Object propertyValue = null;
        if(propertyDefinition.isEmbeddedAttribute()) {
            // Ensure the root embeddable field is declared in our entity, and not some other subclass
            final Field rootEmbeddableField = propertyDefinition.getEmbeddablePath().getStart().getField();
            if(ReflectionUtils.hasField(entity, rootEmbeddableField)) {
                Object currentElement = entity;
                // Traverse the path of embeddables until we reach the leaf
                for(FieldPath.FieldNode node : propertyDefinition.getEmbeddablePath()) {
                    currentElement = BeanPropertyHandler.getValue(currentElement, node.getName());
                    if(currentElement == null) {
                        return null; // Can only loop threw embedded properties when they are not null
                    }
                }
                // Retrieve the property from our embeddable instance
                propertyValue = BeanPropertyHandler.getValue(currentElement, propertyDefinition.getName());
            }
        } else if(ReflectionUtils.hasField(entity, propertyDefinition.getField())) {
            // Ensure the field has been defined in our entity, and not some subclass
            propertyValue = BeanPropertyHandler.getValue(entity, propertyDefinition.getName());
        }
        return propertyValue;
    }
    
    private CellValue createCellValue(Object value) {
        CellValue cellValue = null;
        if(value == null) {
            cellValue = new EmptyValue();
        } else {
            final Class<?> valueType = value.getClass();
            if(String.class.equals(valueType)) {
                cellValue = new StringValue((String) value);
            } if(Boolean.class.isAssignableFrom(valueType)) {
                cellValue = new BooleanValue((Boolean) value);
            } else if(Date.class.isAssignableFrom(valueType)) {
                cellValue = new DateValue((Date) value);
            } else if(Number.class.isAssignableFrom(valueType)) {
                cellValue = new NumericValue((Number) value);
            } else {
                // Convert the value into a string and store it as such
                final String valueAsString = valueConversionService.convert(value, String.class);
                cellValue = new StringValue(valueAsString);
            }
        }
        return cellValue;
    }

}
