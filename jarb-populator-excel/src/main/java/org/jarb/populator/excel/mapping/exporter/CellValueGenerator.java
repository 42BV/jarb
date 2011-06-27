package org.jarb.populator.excel.mapping.exporter;

import java.util.Date;

import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.workbook.BooleanValue;
import org.jarb.populator.excel.workbook.CellValue;
import org.jarb.populator.excel.workbook.DateValue;
import org.jarb.populator.excel.workbook.EmptyValue;
import org.jarb.populator.excel.workbook.NumericValue;
import org.jarb.populator.excel.workbook.StringValue;

/**
 * Converts values of any type into a cell value.
 * @author Jeroen van Schagen
 * @since 27-06-2011
 */
public class CellValueGenerator {
    /** Converts property values into any desired format. **/
    private final ValueConversionService valueConversionService;
    
    /**
     * Construct a new {@link CellValueGenerator}.
     * @param valueConversionService converts values
     */
    public CellValueGenerator(ValueConversionService valueConversionService) {
        this.valueConversionService = valueConversionService;
    }
    
    /**
     * Create a cell value based on some raw value.
     * @param value raw value being stored
     * @return cell value containing our raw value
     */
    public CellValue asCellValue(Object value) {
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
                // Unrecognized value types are converted to string
                cellValue = new StringValue(toString(value));
            }
        }
        return cellValue;
    }
    
    /**
     * Convert a value into its string representation.
     * @param value the value being converted
     * @return string representation of value
     */
    private String toString(Object value) {
        return valueConversionService.convert(value, String.class);
    }
    
}
