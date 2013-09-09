package org.jarbframework.populator.excel.mapping.exporter;

import java.util.Date;

import org.jarbframework.populator.excel.mapping.ValueConversionService;

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
    public Object asCellValue(Object value) {
    	Object cellValue = value;
    	if (value != null) {
    		if (!( value instanceof Number || value instanceof Date || value instanceof Boolean || value instanceof CharSequence)) {
    			cellValue = valueConversionService.convert(value, String.class);
    		}
    	}
        return cellValue;
    }

}
