package org.jarb.populator.excel.mapping.exporter;

import java.util.Date;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnType;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
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

/**
 * Default implementation of {@link EntityExporter}.
 * <b>Note that this component does not work yet!</b>
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class DefaultEntityExporter implements EntityExporter {
    private final ValueConversionService valueConversionService;
    
    public DefaultEntityExporter(ValueConversionService valueConversionService) {
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
                if(BeanPropertyHandler.hasProperty(entity, propertyDefinition.getName())) {
                    Object propertyValue = BeanPropertyHandler.getValue(entity, propertyDefinition.getName());
                    row.getCellAt(propertyDefinition.getColumnName()).setCellValue(createCellValue(propertyValue));
                }
            } else if(columnType == ColumnType.JOIN_COLUMN) {
                
            } else if(columnType == ColumnType.JOIN_TABLE) {
                
            }
        }
        if(classDefinition.hasDiscriminatorColumn()) {
            final String discriminatorValue = classDefinition.getDiscriminatorValue(entity.getClass());
            row.getCellAt(classDefinition.getDiscriminatorColumnName()).setCellValue(new StringValue(discriminatorValue));
        }
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
