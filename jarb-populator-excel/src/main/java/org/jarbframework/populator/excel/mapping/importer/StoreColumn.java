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
import org.jarbframework.utils.bean.DynamicBeanWrapper;
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
     * @param entityDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public void storeValue(Workbook excel, EntityDefinition<?> entityDefinition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow) {
        Sheet sheet = excel.getSheet(entityDefinition.getTableName());

        WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(entityDefinition, excel);
        Integer columnPosition = worksheetDefinition.getColumnPosition(columnDefinition.getColumnName());

        Object cellValue = getCellValue(sheet, rowPosition, columnPosition);
        logger.debug("field: " + columnDefinition.getName() + " column: " + columnDefinition.getColumnName() + " value:[" + cellValue + "]");

        if (cellValue != null) {
            Object bean = excelRow.getCreatedInstance();
            if (columnDefinition.isEmbeddedAttribute()) {
                bean = getOrCreatePathLeaf(bean, columnDefinition.getEmbeddablePath());
            }
            if (DynamicBeanWrapper.wrap(bean).isWritableProperty(columnDefinition.getName())) {
                setExcelRowFieldValue(bean, columnDefinition.getName(), cellValue);
            }
        }
    }

    private Object getCellValue(Sheet sheet, Integer rowPosition, Integer columnPosition) {
        Object cellValue = null;
        if (columnPosition != null) {
            cellValue = sheet.getValueAt(rowPosition, columnPosition);
        }
        return cellValue;
    }

    private Object getOrCreatePathLeaf(Object root, PropertyPath path) {
        Object current = root;
        for (PropertyNode node : path) {
            DynamicBeanWrapper<?> modifiableBean = DynamicBeanWrapper.wrap(current);
            Object value = modifiableBean.getPropertyValue(node.getName());
            if (value == null) {
                value = BeanUtils.instantiateClass(node.getField().getType());
                modifiableBean.setPropertyValue(node.getName(), value);
            }
            current = value;
        }
        return current;
    }

    private void setExcelRowFieldValue(Object bean, String propertyName, Object value) {
        Class<?> propertyType = getPropertyType(new PropertyReference(bean.getClass(), propertyName));
        try {
            Object convertedValue = conversionService.convert(value, propertyType);
            DynamicBeanWrapper.wrap(bean).setPropertyValue(propertyName, convertedValue);
        } catch (CouldNotConvertException e) {
            logger.warn("Could not convert '{}' into a {}, thus '{}' will remain unchanged.", new Object[] { value, propertyType, propertyName });
        }
    }

}
