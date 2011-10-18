package org.jarbframework.populator.excel.mapping.importer;

import static org.jarbframework.utils.bean.BeanProperties.getPropertyType;

import org.jarbframework.populator.excel.mapping.CouldNotConvertException;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyNode;
import org.jarbframework.populator.excel.metamodel.PropertyPath;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.bean.PropertyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Stores the value of a regular Excel column.
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreColumn {
    private static final Logger logger = LoggerFactory.getLogger(StoreColumn.class);

    private ValueConversionService conversionService;

    public StoreColumn(ValueConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Stores a Column in ExcelRow.
     * @param excel Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public void storeValue(Workbook excel, EntityDefinition<?> classDefinition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow) {
        WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        Integer columnPosition = worksheetDefinition.getColumnPosition(columnDefinition.getColumnName());

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        Object cellValue = getCellValue(sheet, rowPosition, columnPosition);
        logger.debug("field: " + columnDefinition.getName() + " column: " + columnDefinition.getColumnName() + " value:[" + cellValue + "]");

        ModifiableBean<Object> propertyAccessor = ModifiableBean.wrap(excelRow.getCreatedInstance());
        if (propertyAccessor.isWritableProperty(columnDefinition.getName())) {
            setExcelRowFieldValue(excelRow.getCreatedInstance(), columnDefinition.getName(), cellValue);
        } else if (columnDefinition.isEmbeddedAttribute()) {
            Object embeddedField = getOrCreatePathLeaf(excelRow.getCreatedInstance(), columnDefinition.getEmbeddablePath());
            setExcelRowFieldValue(embeddedField, columnDefinition.getName(), cellValue);
        }
    }

    private Object getOrCreatePathLeaf(Object root, PropertyPath path) {
        Object current = root;
        for (PropertyNode node : path) {
            ModifiableBean<?> modifiableBean = ModifiableBean.wrap(current);
            Object value = modifiableBean.getPropertyValue(node.getName());
            if (value == null) {
                value = BeanUtils.instantiateClass(node.getField().getType());
                modifiableBean.setPropertyValue(node.getName(), value);
            }
            current = value;
        }
        return current;
    }

    /**
     * Sets an Excelrow's field value via reflection. Works for regular fields, embedded fields and Enumerations.
     * @param bean Excelrow to add the field value to
     * @param fieldName Name of field which value is to be set
     * @param cellValue Value of the field that is to be saved
     */
    private void setExcelRowFieldValue(Object bean, String fieldName, Object cellValue) {
        ModifiableBean<?> modifiableBean = ModifiableBean.wrap(bean);
        if (modifiableBean.isWritableProperty(fieldName)) {
            Class<?> fieldType = getPropertyType(new PropertyReference(bean.getClass(), fieldName));
            try {
                Object fieldValue = conversionService.convert(cellValue, fieldType);
                modifiableBean.setPropertyValue(fieldName, fieldValue);
            } catch (CouldNotConvertException e) {
                logger.warn("Could not convert '{}' into a {}, thus '{}' will remain unchanged.", new Object[] { cellValue, fieldType, fieldName });
            }
        }
    }

    /**
     * Returns the cellValue from the excel file.
     * @param excel Excel file to get cellValue from
     * @param rowPosition Rowposition of the cell
     * @param columnPosition ColumnPosition of the cell
     * @return CellValue from the specified location
     */
    private Object getCellValue(Sheet sheet, Integer rowPosition, Integer columnPosition) {
        Object cellValue = null;
        if (columnPosition != null) {
            cellValue = sheet.getValueAt(rowPosition, columnPosition);
        }
        return cellValue;
    }
}
